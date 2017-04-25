package apilayer.handlers;


import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

/**
 * Created by martinsenden on 2017-04-25.
 */

@Slf4j
public class CreateUserHandler {

    public static Object handleCreateUser(Request request, Response response){
            String email = request.queryParams("email");
            String password = request.queryParams("rawPassword");

            if (!EmailValidator.getInstance().isValid(email)){
                throw halt(400, "Incorrect email");
            }

            User user = new User();

            user.setEmail(email);
            user = User.createUserHelper(user, password);

            Transaction tx = null;
            try(Session session = HibernateUtil.getInstance().openSession()){
                tx = session.beginTransaction();
                session.save(user);
                tx.commit();
            }
            catch(Exception e){
                if (tx != null) {
                    tx.rollback();
                }
                log.error("error during sql", e);
            }
            response.redirect("/");

            return "";
    }
}
