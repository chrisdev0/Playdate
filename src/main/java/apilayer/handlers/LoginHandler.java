package apilayer.handlers;


import lombok.extern.slf4j.Slf4j;

import spark.Request;
import spark.Response;


@Slf4j
public class LoginHandler {





    /** Hanterar utloggning av användaren
     * */
    public static Object logOut(Request request, Response response) {
        request.session().invalidate();
        response.redirect(Paths.StaticFilePaths.INDEX_HTML);
        return "";
    }



}
