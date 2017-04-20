package apilayer.handlers;

import apilayer.RequestHandler;
import com.google.gson.Gson;
import model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import utils.Utils;

import java.util.List;
import java.util.Optional;

import static spark.Spark.halt;

public class LoginTryHandler implements RequestHandler {

    private static Logger logger = LoggerFactory.getLogger(LoginTryHandler.class);

    private SessionFactory sessionFactory;

    public LoginTryHandler(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /** Hanterar en inloggningsrequest
     *
     *  Plockar först fram username och password-värdena från <form> som skickas i POST
     *  Kollar att dessa värden inte är null eller inte är tomma
     *
     *  Öppnar sedan en session från hibernate, eftersom det bara är att läsa så behövs ingen transaction
     *  Skapar en (hibernate) Query som försöker hitta en användare som har
     *  email = username från form och
     *  password = password från formen
     *
     *  Om resultatet inte är en lista med ett objekt så finns det ingen användare med det användarnamnet
     *  och lösenordet. (om det är mer än en användare så är det något fel i koden/databasen
     *
     *  Finns användaren så skickas ett meddelande om success och en session sätts.
     *  Om användaren inte finns så skickas ett meddelande om att användaren inte finns
     *  eller att lösenordet är felaktigt.
     *
     *  om username eller password är null eller tomma så skickas error code 500 tillbaka som svar.
     * */
    @Override
    public Object handle(Request request, Response response) {
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        try {
            Optional<User> userOpt = checkEmailAndPasswordExist(username, password);
        } catch (Exception e) {
            logger.info("Illegal login try using following query params:");
            request.queryParams().forEach(logger::info);
            logger.info("End of query params");
        }
    }

    private void setUserSessionLoggedIn(User user) {

    }


    public Optional<User> checkEmailAndPasswordExist(String email, String password) {
        if (Utils.isNotNullAndNotEmpty(email) && Utils.isNotNullAndNotEmpty(password)) {
            try (Session session = sessionFactory.openSession()) {
                String hql = "FROM User WHERE upper(email) = :email AND password = :password";
                Query query = session.createQuery(hql);
                query.setParameter("email", email);
                query.setParameter("password", password);
                List result = query.list();
                if (result.size() != 1) {
                    return new Gson().toJson("{error: 'Felaktigt användarnamn/lösenord')} ");
                } else {
                    //set logged in
                    User user = (User) result.get(0);
                    request.session(true);
                    request.session().attribute("user", user);
                    return new Gson().toJson("{login: 'success'}");
                }
            }
        } else {
            return halt(500);
        }
    }



}
