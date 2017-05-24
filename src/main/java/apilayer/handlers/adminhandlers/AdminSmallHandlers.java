package apilayer.handlers.adminhandlers;

import apilayer.Constants;
import apilayer.handlers.asynchandlers.SparkHelper;
import dblayer.HibernateUtil;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.Comment;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;

import java.util.Optional;

import static apilayer.Constants.MSG.*;
import static apilayer.handlers.asynchandlers.SparkHelper.getUserFromRequestById;
import static apilayer.handlers.asynchandlers.SparkHelper.getUserFromSession;
import static apilayer.handlers.asynchandlers.SparkHelper.setStatusCodeAndReturnString;

@Slf4j
public class AdminSmallHandlers {

    public static Object handleRemoveComment(Request request, Response response) {

        if (removeComment(ParserHelpers.parseToLong(request.queryParams("commentId")))) {
            return setStatusCodeAndReturnString(response, 200, OK);
        }
        return setStatusCodeAndReturnString(response, 400, ERROR);
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

    public static Object handleRemoveReport(Request request, Response response) {
        if (removeReport(ParserHelpers.parseToLong(request.queryParams("reportId")))) {
            return setStatusCodeAndReturnString(response, 200, OK);
        }
        return setStatusCodeAndReturnString(response, 400, ERROR);
    }


    private static boolean removeReport(Long id) {
        boolean ret = false;
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            int row = session.createQuery("DELETE FROM Report r WHERE r.id = :reportid")
                    .setParameter("reportid", id).executeUpdate();
            tx.commit();
            if (row == 1) {
                ret = true;
            }
        } catch (Exception e) {
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


    public static Object handleRemoveUser(Request request, Response response) {
        Optional<User> optional = getUserFromRequestById(request);
        if (optional.isPresent()) {
            if (UserDAO.getInstance().removeAllOfUser(optional.get())) {
                return setStatusCodeAndReturnString(response, 200, OK);
            }
        }
        return setStatusCodeAndReturnString(response, 400, ERROR);
    }


}
