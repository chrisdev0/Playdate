package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.tools.internal.jxc.ap.Const;
import dblayer.HibernateUtil;
import dblayer.PlaceDAO;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;
import utils.Utils;
import java.util.Optional;
import static apilayer.Constants.MSG.*;
import static apilayer.handlers.asynchandlers.SparkHelper.*;
import static spark.Spark.halt;

@Slf4j
public class PlaydateHandler {

    /** Skapar en Playdate med hjälp av den data som skickas in
     *  kollar att header och description inte är tomma
     *
     *  Om en playdate kan skapas så redirectas användaren till sidan för den playdaten
     *  vid olika fel så returneras statuskoden 400
     * */
    public static Object handleMakePlaydate(Request request, Response response) {
        String header = request.queryParams("header");
        String description = request.queryParams("description");

        if (!Utils.validateLengthOfString(Constants.SHORTDESCMIN, Constants.SHORTDESCMAX, header) ||
                !Utils.validateLengthOfString(Constants.LONGDESCMIN, Constants.LONGDESCMAX, description)) {
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.VALIDATION_ERROR);

        }
        Integer visibilityId = ParserHelpers.parseToInt(request.queryParams("visibilityId"));
        Optional<Place> placeOptional = getPlaceFromRequest(request);
        Long startTime = ParserHelpers.parseToLong(request.queryParams("startTime"));
        PlaydateVisibilityType playdateVisibilityType;

        if (!Utils.validateStartTime(startTime)){
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.VALIDATION_ERROR);
        }
        if (!Utils.isNotNullAndNotEmpty(header, description)) {
            log.error("header or description is empty");
            response.status(400);
            return "missing_info";
        }
        try {
            playdateVisibilityType = PlaydateVisibilityType.intToPlaydateVisibilityType(visibilityId);
        } catch (Exception e) {
            log.error("visibility type was illegal: " + visibilityId);
            throw halt(400, "illegal visbilitytype");
        }
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        if (!placeOptional.isPresent() || user == null) {
            log.error("user is null or couldnt find place with id = " + request.queryParams(Paths.QueryParams.PLACE_BY_ID));
            response.status(400);
            return "";
        }
        Playdate playdate = new Playdate(header, description, startTime, user, placeOptional.get(), playdateVisibilityType);
        Optional<Playdate> playdateOptional = PlaydateDAO.getInstance().saveNewPlaydate(playdate);
        if (playdateOptional.isPresent()) {
            response.status(200);
            String redirect = Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.PLAYDATE_BY_ID + "=" + playdateOptional.get().getId();
            log.info("created playdate successfully, redirecting to " + redirect);
            response.redirect(redirect);
        } else {
            log.error("couldn't create playdate");
            response.status(400);
        }
        return "";
    }


    /** Hanterar att ta bort en Playdate
     *  Ta även bort alla invites till playdaten
     * */
    public static Object handleDeletePlaydate(Request request, Response response) {
        Optional<Playdate> playdateById = getPlaydateFromRequest(request);
        if (playdateById.isPresent()) {
            if (playdateById.get().userIsOwner(getUserFromSession(request))) {
                if (PlaydateDAO.getInstance().deletePlaydate(playdateById.get())) {
                    return setStatusCodeAndReturnString(response,200, OK);
                }
            } else {
                return setStatusCodeAndReturnString(response, 400, USER_IS_NOT_OWNER_OF_PLAYDATE);
            }
        } else {
            return setStatusCodeAndReturnString(response, 400, NO_PLAYDATE_WITH_ID);
        }
        return setStatusCodeAndReturnString(response, 400, ERROR);
    }

    public static Object handleUpdatePlaydate(Request request, Response response){ //put
        String header = request.queryParams("header");
        String description = request.queryParams("description");
        Long startTime = ParserHelpers.parseToLong(request.queryParams("startTime"));
        Integer visibilityId = ParserHelpers.parseToInt(request.queryParams("visibilityId"));
        PlaydateVisibilityType playdateVisibilityType;

        if (!Utils.validateLengthOfString(Constants.SHORTDESCMIN, Constants.SHORTDESCMAX, header) ||
                !Utils.validateLengthOfString(Constants.LONGDESCMIN, Constants.LONGDESCMAX, description)) {
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.VALIDATION_ERROR);

        }
        if (!Utils.validateStartTime(startTime)){
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.VALIDATION_ERROR);
        }

        try {
            playdateVisibilityType = PlaydateVisibilityType.intToPlaydateVisibilityType(visibilityId);
        } catch (Exception e) {
            return setStatusCodeAndReturnString(response, 400, ERROR);
        }
        Optional<Playdate> playdateOptional = getPlaydateFromRequest(request);
        Optional<Place> placeById = getPlaceFromRequest(request);
        if (playdateOptional.isPresent() && placeById.isPresent()) {
            Playdate playdate = playdateOptional.get();
            playdate.setHeader(header);
            playdate.setDescription(description);
            playdate.setStartTime(startTime);
            playdate.setPlaydateVisibilityType(playdateVisibilityType);
            playdate.setPlace(placeById.get());
            if (PlaydateDAO.getInstance().updatePlaydate(playdate)) {
                response.redirect(Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.PLAYDATE_BY_ID + "=" + playdateOptional.get().getId());
                return "";
            } else {
                log.error("error saving updated playdate");
            }
        }
        return setStatusCodeAndReturnString(response, 400, ERROR);
    }

    public static Object removePlaydateAttendance(Request request, Response response){
        Optional<Playdate> playdateOptional = getPlaydateFromRequest(request);
        User user = getUserFromSession(request);
        if (playdateOptional.isPresent()) {
            if (PlaydateDAO.getInstance().removeAttendance(playdateOptional.get(), user)) {
                return setStatusCodeAndReturnString(response, 200, OK);
            } else {
                log.error("couldn't remove playdate attendance playdateId=" + playdateOptional.get().getId() + " for userId=" + user.getId());
            }
        } else {
            return setStatusCodeAndReturnString(response, 400, NO_PLAYDATE_WITH_ID);
        }
        return setStatusCodeAndReturnString(response, 400, ERROR);
    }

    public static Object handleGetAttendanceForPlaydate(Request request, Response response) {
        Optional<Playdate> playdateOptional = getPlaydateFromRequest(request);
        if (playdateOptional.isPresent()) {
            PlaydateDAO.getInstance().refreshPlaydate(playdateOptional.get());
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                    .create().toJson(playdateOptional.get().getParticipants());
        }
        return setStatusCodeAndReturnString(response, 400, NO_PLAYDATE_WITH_ID);
    }

    public static Object handleGetPublicPlaydatesOfPlace(Request request, Response response) {
        Optional<Place> placeOptional = getPlaceFromRequest(request);
        if (placeOptional.isPresent()) {
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                    .create().toJson(PlaydateDAO.getInstance().getPlaydateAtPlace(placeOptional.get()));
        }
        return setStatusCodeAndReturnString(response, 400, NO_PLACE_WITH_ID);
    }

}
