package apilayer.handlers;

import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.Comment;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

@Slf4j
public class CommentHandler {

    public static Object handlePostComment(Request request, Response response) {
        String placeIdStr = request.queryParams("placeId");
        try {
            Long placeId = Long.parseLong(placeIdStr);
            String commentStr = request.queryParams("comment");
            Comment comment = new Comment(commentStr, request.session().attribute("user"), false);
            Transaction tx = null;
            try (Session session = HibernateUtil.getInstance().openSession()) {
                tx = session.beginTransaction();
                Place place = session.get(Place.class, placeId);
                place.addComment(comment);
                session.update(place);
                session.save(comment);
                tx.commit();
                response.redirect(Paths.PROTECTED + Paths.GETONEPLACE + "?placeId=" + placeId);
            } catch (Exception e) {
                log.error("error saving comment", e);
                if (tx != null) {
                    tx.rollback();
                }
            }
        } catch (NumberFormatException e) {
            log.error("couldn't parse place id of post comment value = " + placeIdStr);
            throw halt(400);
        }
        for (String s : request.queryParams()) {
            log.info("got comment request query params " + s + "= " + request.queryParams(s));
        }
        throw halt(400);
    }

}
