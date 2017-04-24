package apilayer.handlers;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

@Slf4j
public class AuthChecker {

    public static boolean isLoggedIn(Request request, Response response) {
        log.info("client " + request.ip() + " checking loginstatus = " + request.session().attribute("user"));
        return request.session().attribute(Constants.USER_SESSION_KEY) != null;
    }

}
