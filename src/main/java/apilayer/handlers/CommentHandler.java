package apilayer.handlers;

import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.Comment;
import model.Place;
import model.Playdate;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

@Slf4j
public class CommentHandler {

    /** Hanterar post av en kommentar på en plats
     *
     *  Vilken plats som får kommentaren beror på vilken id som
     *  skickas in som 'placeId'
     *
     *  Skapar en kommentar och lägger till den hos place och sparar kommentaren
     *  och uppdaterar place
     *
     *  Kommentaren fås fram genom 'comment' och user kan hämtas
     *  ur session hos request-objektet.
     *
     *  todo just nu returnerar den halt(400), bör ändras så att den
     *  todo returnerar alla kommentarer för platsen vilket sedan kan
     *  todo ritas upp med hjälp av javascript
     *
     * @param request request för post av kommentaren
     * @param response response for post av kommentaren
     * */
    public static Object handlePostComment(Request request, Response response) {
        String placeIdStr = request.queryParams("placeId");
        try {
            Long placeId = Long.parseLong(placeIdStr);
            String commentStr = request.queryParams("comment");
            Comment comment = new Comment(commentStr, request.session().attribute("user"), false, null);
            Transaction tx = null;
            try (Session session = HibernateUtil.getInstance().openSession()) {
                tx = session.beginTransaction();
                Place place = session.get(Place.class, placeId);
                if (place == null) {
                    return halt(400);
                }
                comment.setPlace(place);
                place.addComment(comment);
                session.update(place);
                session.save(comment);
                tx.commit();
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
        throw halt(400);
    }

    public static Object handleRemoveComment(Request request, Response response) {
        String commentIdStr = request.queryParams("commentId");
        try {
            Long commentId = Long.parseLong(commentIdStr);
            Transaction tx = null;
            try (Session session = HibernateUtil.getInstance().openSession()) {
                tx = session.beginTransaction();
                Comment comment = session.get(Comment.class, commentId);
                Place place = comment.getPlace();
                place.removeComment(comment);
                Hibernate.initialize(place.getComments());
                session.delete(comment);
                tx.commit();
            } catch (Exception e) {
                if (tx != null) {
                    log.error("Error during remove comment ", e);
                    tx.rollback();
                }
                throw halt(400);
            }
            log.info("removed comment id = " + commentId);
            throw halt(200);
        } catch (NumberFormatException e) {
            log.error("couldn't parse commentid = " + commentIdStr);
            throw halt(400);
        }
    }

}
