package apilayer.handlers;


import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;
import utils.Utils;

import static spark.Spark.*;

@Slf4j
public class CreateUserHandler {

    /** Denna metod hanterar skapandet av en användare
     *  I request så ska följande queryParams skickas in
     *  email           email för användaren
     *  rawPassword     lösenordet (som ska saltas och hashas)
     *
     *  emailen     kontrolleras så att den är korrekt (formaterad)
     *  rawPassword kontrolleras så att den inte innehåller förbjudna
     *              tecken såsom " " samt att den inte är null eller tom
     *
     *  rawPassword saltas och hashas och sparas som password i User
     *  med hjälp av User.createUserHelper som då även spara saltet i User
     *
     *
     * */
    public static Object handleCreateUser(Request request, Response response){
        String email = request.queryParams("email");
        String password = request.queryParams("rawPassword");
        if (!EmailValidator.getInstance().isValid(email)) {
            throw halt(400, "Incorrect email");
        }
        if (Utils.ValidationHelpers.passwordContainsIllegalChar(password)) {
            throw halt(400, "Incorrect password");
        }
        User user = new User(email);
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
