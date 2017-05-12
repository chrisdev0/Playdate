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

import static apilayer.handlers.asynchandlers.SparkHelper.getPlaydateFromRequest;
import static apilayer.handlers.asynchandlers.SparkHelper.setStatusCodeAndReturnString;
import static spark.Spark.delete;
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
        Integer visibilityId = ParserHelpers.parseToInt(request.queryParams("visibilityId"));;
        Long placeId = ParserHelpers.parseToLong(request.queryParams("placeId"));
        Long startTime = ParserHelpers.parseToLong(request.queryParams("startTime"));
        PlaydateVisibilityType playdateVisibilityType;
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
        Optional<Place> placeOptional = PlaceDAO.getInstance().getPlaceById(placeId);
        if (!placeOptional.isPresent() || user == null) {
            log.error("user is null or couldnt find place with id = " + placeId);
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
        String playdateId = request.queryParams("playdateId");
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        try {
            Long lId = Long.parseLong(playdateId);
            Optional<Playdate> playdateById = PlaydateDAO.getInstance().getPlaydateById(lId);
            if (playdateById.isPresent()) {
                if (playdateById.get().userIsOwner(user)) {
                    if (PlaydateDAO.getInstance().deletePlaydate(playdateById.get())) {
                        response.status(200);
                        return "";
                    }
                } else {
                    response.status(401);
                    return "user_not_owner";
                }
            } else {
                response.status(400);
                return "no_playdate_with_id";
            }
        } catch (NullPointerException | NumberFormatException e) {
            log.error("client: " + request.ip() + " sent illegal playdate id = " + playdateId + "error = " + e.getMessage());
        }
        throw halt(400);
    }

    public static Object handleUpdatePlaydate(Request request, Response response){ //put
        String header = request.queryParams("header");
        String description = request.queryParams("description");
        Long playdateId = ParserHelpers.parseToLong(request.queryParams("playdateId"));
        Long placeId = ParserHelpers.parseToLong(request.queryParams("placeId"));
        Long startTime = ParserHelpers.parseToLong(request.queryParams("startTime"));
        Integer visibilityId = ParserHelpers.parseToInt(request.queryParams("visibilityId"));
        PlaydateVisibilityType playdateVisibilityType;
        try {
            playdateVisibilityType = PlaydateVisibilityType.intToPlaydateVisibilityType(visibilityId);
        } catch (Exception e) {
            log.error("Illegal visibilityType", e);
            throw halt(400, e.getMessage());
        }
        Optional<Playdate> playdateOptional = PlaydateDAO.getInstance().getPlaydateById(playdateId);
        Optional<Place> placeById = PlaceDAO.getInstance().getPlaceById(placeId);
        if (playdateOptional.isPresent() && placeById.isPresent()) {
            Playdate playdate = playdateOptional.get();
            playdate.setHeader(header);
            playdate.setDescription(description);
            playdate.setStartTime(startTime);
            playdate.setPlaydateVisibilityType(playdateVisibilityType);
            playdate.setPlace(placeById.get());
            if (PlaydateDAO.getInstance().updatePlaydate(playdate)) {
                response.redirect(Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.PLAYDATE_BY_ID + "=" + playdateId);
                return "";
            } else {
                log.error("error saving updated playdate");
            }
        } else {
            log.error("couldn't find playdate with id = " + playdateId + " or Place with id = " + placeId);
        }
        response.status(400);
        return "";
    }

    /** todo Flytta saker till DAO
     * */
    @Deprecated
    public static Object removePlaydateAttendance(Request request, Response response){

        Session session = null;
        Transaction tx = null;
        String playdateId = request.queryParams("playdateId");
        long lId;

        try {
            lId = Long.parseLong(playdateId);
            log.info("Trying to change attendence for playdate with id = " + lId);
        } catch (NullPointerException | NumberFormatException e) {
            log.error("client: " + request.ip() + " sent illegal playdate id = " + playdateId + "error = " + e.getMessage());
            throw halt(400);
        }

        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            User user = request.session().attribute(Constants.USER_SESSION_KEY);
            Playdate playdate = session.get(Playdate.class, lId);

            if (playdate == null) {
                log.error("playdate is null");
                throw halt(400);
            }
            if (user == null) {
                log.error("user is null");
                throw halt(400);
            }

            if (playdate.getOwner().equals(user)) {
                log.error("user is owner of playdate, can't be removed");
                throw halt(400);
            }

            for (User u : playdate.getParticipants()) {
                if (!user.equals(u)) {
                    log.error("user is not a participant");
                    throw halt(400);
                }
                user.removeAttendingPlaydate(playdate);
                playdate.removeParticipant(user);
            }

            session.update(playdate);
            session.update(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return halt(400);
    }

    public static Object handleGetAttendanceForPlaydate(Request request, Response response) {
        Optional<Playdate> playdateOptional = getPlaydateFromRequest(request);
        if (playdateOptional.isPresent()) {
            PlaydateDAO.getInstance().refreshPlaydate(playdateOptional.get());
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                    .create().toJson(playdateOptional.get().getParticipants());
        }
        return setStatusCodeAndReturnString(response, 400, Constants.MSG.NO_PLAYDATE_WITH_ID);
    }

}
