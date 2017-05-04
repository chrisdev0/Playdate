package apilayer.handlers;

import apilayer.Constants;
import com.google.gson.Gson;
import dblayer.HibernateUtil;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.Comment;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;

import java.util.Optional;
import java.util.Set;

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
     * @param request request för post av kommentaren
     * @param response response for post av kommentaren
     *
     * @return      returnerar JSON med alla kommentarer
     * // TODO: 2017-05-04
     * todo tvätta bort värden som inte ska skickas ut till frontend, exempelvis fb access token
     * */
    public static Object handlePostComment(Request request, Response response) {
        try {
            Long placeId = Long.parseLong(request.queryParams("placeId"));
            String commentStr = request.queryParams("comment");
            Comment comment = new Comment(commentStr, request.session().attribute(Constants.USER_SESSION_KEY), false);
            Optional<Place> placeOptional = PlaceDAO.getInstance().getPlaceById(placeId);
            if (placeOptional.isPresent()) {
                placeOptional.get().addComment(comment);
                Optional<Set<Comment>> comments = PlaceDAO.getInstance().saveComment(comment, placeOptional.get());
                if (comments.isPresent()) {
                    return new Gson().toJson(comments.get());
                } else {
                    response.status(400);
                    return "";
                }
            } else {
                log.error("couldn't find place to post comment in");
                response.status(400);
                return "";
            }
        } catch (NumberFormatException e) {
            response.status(400);
            return "";
        }
    }

    /*
    /** Hanterar att ta bort en kommentar
     *
     * @return halt(200) om kommentaren hittades och kunde tas bort
     *
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
    */

}
