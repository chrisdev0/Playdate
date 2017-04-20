package apilayer.handlers;

import apilayer.RequestHandler;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.List;

import static spark.Spark.halt;

public class LoginTryHandler implements RequestHandler {

    private static Logger logger = LoggerFactory.getLogger(LoginTryHandler.class);

    private static final String USERNAME_STRING_START = "username=";
    private static final String PASSWORD_STRING_START = "password=";

    private SessionFactory sessionFactory;

    public LoginTryHandler(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Object handle(Request request, Response response) {
        String body = request.body().trim();
        String[] split = body.split("&", 3);
        logger.info(Arrays.toString(split));
        if (split.length != 2 && containsUsernameAndPassword(split[0], split[1])) {
            try (Session session = sessionFactory.openSession()) {
                String hql = "FROM User WHERE upper(email) = :email AND password = :password";
                Query query = session.createQuery(hql);
                List result = query.list();
                if (result.size() != 1) {

                }

            }
        } else {
            return halt(500);
        }
        return null;
    }

    /** Returnerar sant om
     *  requesten som skickas in
     *
     * */
    private boolean containsUsernameAndPassword(String userSplit, String passwordSplit) {
        return userSplit.startsWith(USERNAME_STRING_START) && passwordSplit.startsWith(PASSWORD_STRING_START);
    }

}
