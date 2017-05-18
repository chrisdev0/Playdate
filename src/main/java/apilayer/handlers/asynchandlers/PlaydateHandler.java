package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.regexp.internal.RE;
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
        Optional<Place> placeOptional = getPlaceFromRequest(request);
        if (!) ||
                ! ||
                !placeOptional.isPresent()) {
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.VALIDATION_ERROR);
        }
        Long startTime = ParserHelpers.parseToLong(request.queryParams("startTime"));
        PlaydateVisibilityType playdateVisibilityType;
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        try {
            playdateVisibilityType = PlaydateVisibilityType.intToPlaydateVisibilityType(ParserHelpers.parseToInt(request.queryParams("visibilityId")));
        } catch (Exception e) {
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.ERROR);
        }
        Optional<Playdate> playdateOptional = PlaydateDAO.getInstance()
                .saveNewPlaydate(new Playdate(header, description, startTime, user, placeOptional.get(), playdateVisibilityType));
        if (playdateOptional.isPresent()) {
            response.redirect(Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.PLAYDATE_BY_ID + "=" + playdateOptional.get().getId());
            return setStatusCodeAndReturnString(response, 200, OK);
        } else {
            log.error("couldn't create playdate");
            return setStatusCodeAndReturnString(response, 400, ERROR);
        }
    }

    private static String getValidationErrorOrNull(String header, String description, Optional<Place> ) {
        String ret = "";
        if (!Utils.validateLengthOfString(3, 30, header)) {
            ret += "_header";
        }
        if (!Utils.validateLengthOfString(20, 300, description)) {
            ret += "_description";
        }
        if(!)
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
