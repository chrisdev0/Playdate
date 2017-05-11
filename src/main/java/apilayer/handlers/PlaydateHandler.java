package apilayer.handlers;

import apilayer.Constants;
import dblayer.HibernateUtil;
import dblayer.PlaceDAO;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.HaltException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;
import utils.Utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

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
        Integer visibilityId = null;
        Long placeId = null;
        Long startTime = null;
        //request.queryMap().toMap().forEach((s, strings) -> log.info("key=" + s + " value= " + Arrays.toString(strings)));
        log.info("placeid = " + request.queryParams("placeId"));
        log.info("visibilityId = " + request.queryParams("visibilityId"));
        log.info("header = " + request.queryParams("header"));
        log.info("startTime= " + request.queryParams("startTime"));
        log.info("description= " + request.queryParams("description"));
        placeId = ParserHelpers.parseToLong(request.queryParams("placeId"));
        startTime = ParserHelpers.parseToLong(request.queryParams("startTime"));
        visibilityId = ParserHelpers.parseToInt(request.queryParams("visibilityId"));
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
            String redirect = Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.GET_ONE_PLAYDATE_BY_ID + "=" + playdateOptional.get().getId();
            log.info("created playdate successfully, redirecting to " + redirect);
            response.redirect(redirect);
        } else {
            log.error("couldn't create playdate");
            response.status(400);
        }
        return "";
    }


    /** Hanterar att hämta och visa en Playdate
     *  Försöker hitta playdate med id
     *  @param request queryParam = playdateId
     *
     *  kontrollerar att detta id är en long och använder sig sen av
     *  GetOnePlaydateHandlerOLD för att hämta och mata ut Playdate-objektet till
     *  templatefilen som anges i GetOnePlaydateHandlerOLD-konstruktorn
     * */
    /*public static ModelAndView handleGetOnePlaydate(Request request, Response response) {
        String id = request.queryParams(Paths.QueryParams.GET_ONE_PLAYDATE_BY_ID);
        try {
            long lId = Long.parseLong(id);
            log.info("fetching playdate with id = " + lId);
            return new GetOnePlaydateHandlerOLD("TODELETE/showplaydatepage.vm", lId, 400)
                    .handleTemplateFileRequest(request, response);
        } catch (NullPointerException | NumberFormatException e) {
            log.error("client: " + request.ip() + " sent illegal playdate id = " + id + " e = " + e.getMessage());
            throw halt(400);
        }
    }*/


    /** Hanterar att ta bort en Playdate
     *  Ta även bort alla invites till playdaten
     * */

    //flytta session utanför try(...) så kan den kan komma åt rollbakc,
    //glöm inte att stänga session
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
                response.redirect(Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.GET_ONE_PLAYDATE_BY_ID + "=" + playdateId);
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

}
