package apilayer.handlers;

import apilayer.Constants;
import dblayer.HibernateUtil;
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
            log.error("", e);
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
            log.info("client: " + request.ip() + " sent illegal playdate id = " + id + " e = " + e.getMessage());
            throw halt(400);
        }
    }

    public static Object handleUpdatePlaydate(Request request, Response response){ //put
        // hämta alla värden från request headern.
        String placeIdStr = request.queryParams("placeId");
        String startTimeStr = request.queryParams("startTime");
        String endTimeStr = request.queryParams("endTime");
        String visiblityId = request.queryParams("visibilityId");
        String header = request.queryParams("header");
        String description = request.queryParams("description");
        String playdateId = request.queryParams("playdateId");
        Long placeId = ParserHelpers.parseToLong(placeIdStr);
        Long startTime = ParserHelpers.parseToLong(startTimeStr);
        Long endTime = ParserHelpers.parseToLong(endTimeStr);
        Integer visibilityId = ParserHelpers.parseToInt(visiblityId);
        PlaydateVisibilityType playdateVisibilityType;

        // if(id på playdate finns och är korrekt)
        /*
        if (place.getId.equals(placeIdStr)
         */

        //      if(place är ändrat)
        //          hämta place från databasen, både den nya och den gamla
        //          ta sedan bort den gamla och ändra place i playdate till den nya
        //
        //    uppdatera sedan playdaten, skapa inte en ny, med session.merge(playdate);

    }
}
