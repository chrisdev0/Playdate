package apilayer.handlers;

import apilayer.Constants;
import dblayer.HibernateUtil;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.delete;
import static spark.Spark.halt;

@Slf4j
public class PlaydateHandler {

    public static Object handleMakePlaydate(Request request, Response response) {
        String placeIdStr = request.queryParams("placeId");
        String startTimeStr = request.queryParams("startTime");
        String visiblityId = request.queryParams("visibilityId");
        String header = request.queryParams("header");
        String description = request.queryParams("description");
        Long placeId = ParserHelpers.parseToLong(placeIdStr);
        Long startTime = ParserHelpers.parseToLong(startTimeStr);
        Integer visibilityId = ParserHelpers.parseToInt(visiblityId);
        PlaydateVisibilityType playdateVisibilityType;
        Long id = null;
        try {
            playdateVisibilityType = PlaydateVisibilityType.intToPlaydateVisibilityType(visibilityId);
        } catch (Exception e) {
            log.error("Illegal visibilityType", e);
            throw halt(400, e.getMessage());
        }
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        if (user == null) {
            log.error("User null when trying to create playdate");
            throw halt(400, "user is null");
        }
        Playdate playdate = new Playdate(header, description, startTime, user, null, playdateVisibilityType);
        Transaction tx = null;
        try (Session session = HibernateUtil.getInstance().openSession()) {
            Place place = session.get(Place.class, placeId);
            if (place == null) {
                throw halt(400, "no place with this id");
            }
            playdate.setPlace(place);
            tx = session.beginTransaction();
            id = (Long)session.save(playdate);
            session.merge(user);
            tx.commit();
            response.status(200);
            if (id != null) {
                response.redirect(Paths.PROTECTED + Paths.GETONEPLAYDATE + "? " + Paths.QueryParams.GET_ONE_PLAYDATE_BY_ID + "=" + id);
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("error during sql", e);
            response.status(500);
        }
        return "";

    }

    /** Hanterar att hämta och visa en Playdate
     *  Försöker hitta playdate med id
     *  @param request queryParam = playdateId
     *
     *  kontrollerar att detta id är en long och använder sig sen av
     *  GetOnePlaydateHandler för att hämta och mata ut Playdate-objektet till
     *  templatefilen som anges i GetOnePlaydateHandler-konstruktorn
     * */
    public static ModelAndView handleGetOnePlaydate(Request request, Response response) {
        String id = request.queryParams(Paths.QueryParams.GET_ONE_PLAYDATE_BY_ID);
        try {
            long lId = Long.parseLong(id);
            log.info("fetching playdate with id = " + lId);
            return new GetOnePlaydateHandler("showplaydatepage.vm", lId, 400)
                    .handleTemplateFileRequest(request, response);
        } catch (NullPointerException | NumberFormatException e) {
            log.error("client: " + request.ip() + " sent illegal playdate id = " + id + " e = " + e.getMessage());
            throw halt(400);
        }
    }


    /** Hanterar att ta bort en Playdate
     * */
    public static Object handleDeletePlaydate(Request request, Response response) {
        String playdateId = request.queryParams("playdateId");
        long lId;

        /*
        Ta även bort alla invites till playdaten
         */

        try {
            lId = Long.parseLong(playdateId);
            log.info("Trying to delete with id = " + lId);
        } catch (NullPointerException | NumberFormatException e) {
            log.error("client: " + request.ip() + " sent illegal playdate id = " + playdateId + "error = " + e.getMessage());
            throw halt(400);
        }

        Transaction tx = null;

        try (Session session = HibernateUtil.getInstance().openSession()) {
            Playdate playdate = session.get(Playdate.class, lId);
            User user = request.session().attribute(Constants.USER_SESSION_KEY);
            if (user == null){
                log.error("User is null");
                throw halt(400);
            }

            if (playdate == null){
                log.error("Playdate is null");
                throw halt(400);
            }

            if (!playdate.getOwner().equals(user)){
                log.error("User is not owner of playdate");
                throw halt(400, "User is not owner of playdate");
            }
            tx = session.beginTransaction();
            session.delete(playdate);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                throw halt(400);
            }
            log.error("Error during Hibernate execution", e);
            throw halt(400);
        }
        return halt(200);
    }

    public static Object handleUpdatePlaydate(Request request, Response response){ //put
        // hämta alla värden från request headern.
        String placeIdStr = request.queryParams("placeId");
        String startTimeStr = request.queryParams("startTime");
        String endTimeStr = request.queryParams("endTime");
        String visiblityId = request.queryParams("visibilityId");
        String header = request.queryParams("header");
        String description = request.queryParams("description");
        String playdateIdString = request.queryParams("playdateId");
        Long playdateId;
        Long placeId = ParserHelpers.parseToLong(placeIdStr);
        Long startTime = ParserHelpers.parseToLong(startTimeStr);
        Integer visibilityId = ParserHelpers.parseToInt(visiblityId);
        PlaydateVisibilityType playdateVisibilityType;

        try {
            playdateId = Long.parseLong(playdateIdString);
            log.info("Trying to find with id = " + playdateId);
        } catch (NullPointerException | NumberFormatException e) {
            log.error("client: " + request.ip() + " sent illegal playdate id = " + playdateIdString + "error = " + e.getMessage());
            throw halt(400);
        }

        try {
            playdateVisibilityType = PlaydateVisibilityType.intToPlaydateVisibilityType(visibilityId);
        } catch (Exception e) {
            log.error("Illegal visibilityType", e);
            throw halt(400, e.getMessage());
        }

        Transaction tx = null;
        try(Session session = HibernateUtil.getInstance().openSession()){
            Playdate playdate = session.get(Playdate.class, playdateId);
            User user = request.session().attribute(Constants.USER_SESSION_KEY);

            if(playdate == null || user == null || !playdate.getOwner().equals(user)){
                log.error("Unable to update playdate: no playdate with id, user not logged in or user is not owner of playdate");
                response.status(400);
                return "";
            }
            playdate.setHeader(header);
            playdate.setStartTime(startTime);
            playdate.setDescription(description);
            playdate.setPlaydateVisibilityType(playdateVisibilityType);
            if (!playdate.getPlace().getId().equals(placeId)) {
                Place placeTemp = session.get(Place.class, placeId);
                playdate.setPlace(placeTemp);
            }
            tx = session.beginTransaction();
            session.update(playdate);
            session.merge(user);
            tx.commit();

        }catch(Exception e){
            if (tx != null) {
                tx.rollback();
            }
        }

        return halt(200);
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

        try{
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            User user = request.session().attribute(Constants.USER_SESSION_KEY);
            Playdate playdate = session.get(Playdate.class, lId);

            if(playdate == null){
                log.error("playdate is null");
                throw halt(400);
            }
            if(user == null){
                log.error("user is null");
                throw halt(400);
            }

            if(playdate.getOwner().equals(user)){
                log.error("user is owner of playdate, can't be removed");
                throw halt(400);
            }

            for(User u :playdate.getParticipants()) {
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

        }catch(Exception e){
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if(session != null){
                session.close();
            }
        }
        return halt(400);
    }
}
