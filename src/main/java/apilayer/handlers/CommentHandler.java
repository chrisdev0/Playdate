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
