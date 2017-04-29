package apilayer.handlers;

import apilayer.Constants;
import apilayer.RequestHandler;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import spark.Request;
import spark.Response;
import utils.PasswordHandler;
import utils.Utils;

import java.util.List;
import java.util.Optional;

import static spark.Spark.halt;

@Slf4j
public class LoginHandler {



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
    public Object handleLoginTry(Request request, Response response) {
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        try {
            Optional<User> userOpt = checkEmailAndPasswordExist(username, password);
            if (userOpt.isPresent()) {
                setUserSessionLoggedIn(request, userOpt.get());
                return returnForLoginCheck(true);
            } else {
                return returnForLoginCheck(false);
            }
        } catch (IllegalArgumentException e) {
            log.info("Illegal login try using following query params:");
            request.queryParams().forEach(log::info);
            log.info("End of query params");
            return halt(401);
        }
    }

    /** Returnerar olika strängar (som finns i Constants) beroende på om login lyckades eller misslyckades
     * */
    private Object returnForLoginCheck(boolean wasSuccessful) {
        return wasSuccessful ? Constants.ON_LOGIN_SUCCESS_RETURN : Constants.ON_LOGIN_FAIL_RETURN;
    }

    /** Sätter användaren som inloggad
     * */
    private void setUserSessionLoggedIn(Request request, User user) {
        request.session(true).attribute(Constants.USER_SESSION_KEY, user);
    }


    /** Returnerar en användare (om den finns) som har email och lösenord som skickas som argument
     *  Om email eller password är null eller tomt så skickas ett IllegalArgumentException
     *
     *  Kör upper(email) i HQL-frågan samt toUpperCase() email. detta så att exempelvis
     *  a@b.com matchar A@b.com och a@b.Com
     * */
    public Optional<User> checkEmailAndPasswordExist(String email, String password) throws IllegalArgumentException{
        if (Utils.isNotNullAndNotEmpty(email) && Utils.isNotNullAndNotEmpty(password)) {
            try (Session session = HibernateUtil.getInstance().openSession()) {
                String hql = "FROM User WHERE upper(email) = :email";
                Query query = session.createQuery(hql);
                query.setParameter("email", email.toUpperCase());
                List result = query.list();
                if (result.size() != 1) {
                    return Optional.empty();
                } else {
                    User user = (User) result.get(0);
                    if (PasswordHandler.validateUserPwd(user, password)) {
                        return Optional.of(user);
                    }
                    return Optional.empty();
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static Object logOut(Request request, Response response) {
        request.session().invalidate();
        response.redirect("/index.html");
        return "";
    }



}
