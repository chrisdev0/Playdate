package apilayer.handlers;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import spark.Request;

@Slf4j
public class AuthChecker {


    /** Metoden returnerar true om användaren är inloggad
     *  loggar användarens ip på info
     * @param request request-objektet där session kan hämtas ifrån.
     * @return om användaren är inloggad
     * */
    public static boolean isLoggedIn(Request request) {
        return request.session().attribute(Constants.USER_SESSION_KEY) != null;
    }

}
