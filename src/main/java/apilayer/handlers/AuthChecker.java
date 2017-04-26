package apilayer.handlers;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

@Slf4j
public class AuthChecker {


    /** Metoden returnerar true om användaren är inloggad
     *  loggar användarens ip på info
     * @param request request-objektet där session kan hämtas ifrån.
     * @param response ingen användning för tillfället
     * @return om användaren är inloggad
     * */
    public static boolean isLoggedIn(Request request, Response response) {
        log.info("client " + request.ip() + " checking loginstatus = " + request.session().attribute("user"));
        return request.session().attribute(Constants.USER_SESSION_KEY) != null;
    }

}
