package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import com.google.gson.GsonBuilder;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
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
        Long startTime = ParserHelpers.parseToLong(request.queryParams("startTime"));
        String validaton = getValidationError(header, description, startTime,  placeOptional);
        if (!validaton.isEmpty()) {
            return setStatusCodeAndReturnString(response, 400, VALIDATION_ERROR + validaton);
        }
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
            String redirectUrl = Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.PLAYDATE_BY_ID + "=" + playdateOptional.get().getId();
            return setStatusCodeAndReturnString(response, 200, redirectUrl);
        } else {
            log.error("couldn't create playdate");
            return setStatusCodeAndReturnString(response, 400, ERROR);
        }
    }

    private static String getValidationError(String header, String description, long startTime, Optional<Place> placeOptional) {
        String ret = "";
        if (!Utils.validateLengthOfString(Constants.SHORTDESCMIN, Constants.SHORTDESCMAX, header)) {
            ret += Constants.MSG.ValidationErrors.ERROR_HEADER;
        }
        if (!Utils.validateLengthOfString(Constants.LONGDESCMIN, Constants.LONGDESCMAX, description)) {
            ret += Constants.MSG.ValidationErrors.ERROR_DESCRIPTION;
        }
        if (!placeOptional.isPresent()) {
            ret += Constants.MSG.ValidationErrors.ERROR_PLACE;
        }
        if(!Utils.validateStartTime(startTime)){
            ret += Constants.MSG.ValidationErrors.ERROR_STARTTIME;
        }
        return ret;
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
        Optional<Place> placeById = getPlaceFromRequest(request);
        String validationError = getValidationError(header, description, startTime, placeById);
        if (!validationError.isEmpty()) {
            return setStatusCodeAndReturnString(response, 400, VALIDATION_ERROR + validationError);
        }
        PlaydateVisibilityType playdateVisibilityType;
        try {
            playdateVisibilityType = PlaydateVisibilityType.intToPlaydateVisibilityType(visibilityId);
        } catch (Exception e) {
            return setStatusCodeAndReturnString(response, 400, ERROR);
        }
        Optional<Playdate> playdateOptional = getPlaydateFromRequest(request);
        if (playdateOptional.isPresent() && placeById.isPresent()) {
            Playdate playdate = playdateOptional.get();
            playdate.setHeader(header);
            playdate.setDescription(description);
            playdate.setStartTime(startTime);
            playdate.setPlaydateVisibilityType(playdateVisibilityType);
            playdate.setPlace(placeById.get());
            if (PlaydateDAO.getInstance().updatePlaydate(playdate)) {
                String redirectUrl = Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.PLAYDATE_BY_ID + "=" + playdateOptional.get().getId();
                return setStatusCodeAndReturnString(response, 200, redirectUrl);
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
                    .create().toJson(PlaydateDAO.getInstance()
                            .getPlaydateAtPlace(placeOptional.get())
                            .stream()
                            .filter(Playdate::playdateIsInFuture)

                    );
        }
        return setStatusCodeAndReturnString(response, 400, NO_PLACE_WITH_ID);
    }

    public static Object handleGetFriendsToInvite(Request request, Response response) {
        User user = getUserFromSession(request);
        Optional<Playdate> playdate = getPlaydateFromRequest(request);
        if (playdate.isPresent()) {
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                    .toJson(PlaydateDAO.getInstance().getPotentialFriendsToInvite(playdate.get()));
        } else {
            return setStatusCodeAndReturnString(response, 400, NO_PLAYDATE_WITH_ID);
        }
    }

}
