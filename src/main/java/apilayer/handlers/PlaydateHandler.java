package apilayer.handlers;

import apilayer.Constants;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Hibernate;
import org.hibernate.Query;
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

    public static Object handleMakePlaydate(Request request, Response response) { //post
        String placeIdStr = request.queryParams("placeId");
        String startTimeStr = request.queryParams("startTime");
        String endTimeStr = request.queryParams("endTime");
        String visiblityId = request.queryParams("visibilityId");
        String header = request.queryParams("header");
        String description = request.queryParams("description");
        Long placeId = ParserHelpers.parseToLong(placeIdStr);
        Long startTime = ParserHelpers.parseToLong(startTimeStr);
        Long endTime = ParserHelpers.parseToLong(endTimeStr);
        Integer visibilityId = ParserHelpers.parseToInt(visiblityId);
        PlaydateVisibilityType playdateVisibilityType;

        try {
            playdateVisibilityType = PlaydateVisibilityType.intToPlaydateVisibilityType(visibilityId);
        } catch (Exception e) {
            log.error("Illegal visibilityType", e);
            throw halt(400, e.getMessage());
        }
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        if (user == null) {
            log.error("User is null");
            throw halt(400, "user is null");
        }

        Playdate playdate = new Playdate(header, description, startTime, endTime, user, null, playdateVisibilityType);
        Transaction tx = null;
        try (Session session = HibernateUtil.getInstance().openSession()) {
            Place place = session.get(Place.class, placeId);
            if (place == null) {
                log.error("no place with id = " + placeIdStr);
                throw halt(400, "no place with this id");
            }
            playdate.setPlace(place);
            tx = session.beginTransaction();
            session.save(playdate);
            session.merge(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("error during sql", e);
        }
        throw halt(200);
    }

    public static ModelAndView handleGetOnePlaydate(Request request, Response response) {
        String id = request.queryParams("playdateId");
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
        Long endTime = ParserHelpers.parseToLong(endTimeStr);
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

           if(playdate == null){
               log.error("playdate is null");
               throw halt(400);
           }
           if(user == null){
               log.error("user is null");
               throw halt(400);
           }
           if(!playdate.getOwner().equals(user)){
               log.error("user is not owner of playdate");
               throw halt(400);
           }

            playdate.setHeader(header);
            playdate.setEndTime(endTime);
            playdate.setStartTime(startTime);
            playdate.setDescription(description);
            playdate.setPlaydateVisibilityType(playdateVisibilityType);

            if(playdate.getPlace().getId()!= placeId){
                Place placeTemp = session.get(Place.class, placeId);
                playdate.setPlace(placeTemp);
            }

            tx = session.beginTransaction();
            session.save(playdate);
            session.merge(user);
            tx.commit();

        }catch(Exception e){
            tx.rollback();
        }

       return halt(200);
    }
}
