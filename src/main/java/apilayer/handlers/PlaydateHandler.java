package apilayer.handlers;

import apilayer.Constants;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import model.PlaydateVisibilityType;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.HaltException;
import spark.Request;
import spark.Response;

import java.io.*;

import static spark.Spark.halt;

@Slf4j
public class PlaydateHandler {

    public static Object handleMakePlaydate(Request request, Response response) {
        String placeIdStr = request.queryParams("placeId");
        String startTimeStr = request.queryParams("startTime");
        String endTimeStr = request.queryParams("endTime");
        String visiblityId = request.queryParams("visibilityId");
        String header = request.queryParams("header");
        String description = request.queryParams("description");
        Long placeId = parseToLong(placeIdStr);
        Long startTime = parseToLong(startTimeStr);
        Long endTime = parseToLong(endTimeStr);
        Integer visibilityId = parseToInt(visiblityId);
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

    public static Integer parseToInt(String str) throws HaltException {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw halt(400);
        }
    }


    public static Long parseToLong(String str) throws HaltException {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw halt(400);
        }
    }
}
