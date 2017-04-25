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
import spark.ModelAndView;
import spark.Request;
import spark.Response;

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

    /*lagt till detta*/
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
}
