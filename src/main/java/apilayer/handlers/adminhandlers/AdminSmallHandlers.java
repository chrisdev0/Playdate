package apilayer.handlers.adminhandlers;

import apilayer.Constants;
import apilayer.handlers.asynchandlers.SparkHelper;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.Comment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;

import static apilayer.handlers.asynchandlers.SparkHelper.setStatusCodeAndReturnString;

@Slf4j
public class AdminSmallHandlers {

    public static Object handleRemoveComment(Request request, Response response) {

        if (removeComment(ParserHelpers.parseToLong(request.queryParams("commentId")))) {
            return setStatusCodeAndReturnString(response, 200, Constants.MSG.OK);
        }
        return setStatusCodeAndReturnString(response, 400, Constants.MSG.ERROR);
    }

    private static boolean removeComment(Long commentId) {
        boolean ret = false;
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            int commentId1 = session.createQuery("DELETE FROM Comment c WHERE c.id = :commentId")
                    .setParameter("commentId", commentId).executeUpdate();
            if (commentId1 == 1) {
                ret = true;
            }
            tx.commit();
        } catch (Exception e) {
            ret = false;
            log.error("error saving", e);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ret;
    }

}
