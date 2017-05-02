package apilayer.route;

import apilayer.fbhandlers.FBConfigFactory;
import apilayer.fbhandlers.FBRouteHandler;
import apilayer.handlers.CreateUserHandler;
import apilayer.handlers.LoginHandler;
import apilayer.handlers.Paths;
import cache.Cache;
import lombok.extern.slf4j.Slf4j;
import model.DBAPIImage;
import org.pac4j.core.config.Config;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.SecurityFilter;
import secrets.Secrets;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static spark.Spark.*;

@Slf4j
public class OpenRoutes {

    /** Initierar de routes som ska leda till statiska template-filer
     *  så att index.html (och "/") leder till att index.vm blir renderat
     * */
    public static void initOpenRoutes() {
        //Hanterar inloggningsförsök
        //post(Paths.TRYLOGIN, new LoginHandler()::handleLoginTry);

        //hanterar registrering av användare
        //post(Paths.DOREG, CreateUserHandler::handleCreateUser);

        //hanterar logout
        get(Paths.LOGOUT, LoginHandler::logOut);

        get(Paths.APIIMAGE + "/:id", (request, response) -> {
            String key = request.params("id");
            if (key == null || key.isEmpty() || key.equals("-1")) {
                response.redirect("/images/testlekplats.png");
                return "";
            }
            log.info("api image key = " + key);
            Cache cache = Cache.getInstance();
            try {
                DBAPIImage dbImage = cache.getDbImage(key);
                HttpServletResponse raw = response.raw();
                ServletOutputStream outputStream = raw.getOutputStream();
                outputStream.write(dbImage.getImageAsByte());
                outputStream.flush();
                outputStream.close();
                return response.raw();
            } catch (ExecutionException e) {
                log.error("unable to load image");
            }
            return "error";
        });
        initializeFacebookLogin();
    }

    private static void initializeFacebookLogin() {
        Secrets secrets = Secrets.getInstance();
        Optional<String> fbAppIdOpt = secrets.getValue("fbAppId");
        Optional<String> fbSaltOpt = secrets.getValue("fbSalt");
        Optional<String> fbSecretOpt = secrets.getValue("fbSecret");
        if (fbAppIdOpt.isPresent() && fbSaltOpt.isPresent() && fbSecretOpt.isPresent()) {
            initFacebookRoutes(fbSaltOpt.get(), fbAppIdOpt.get(), fbSecretOpt.get());
        } else {
            log.error("missing facebook values");
            stop();
            return;
        }
    }

    /** Initierar routes för facebooklogin
     *
     * */
    public static void initFacebookRoutes(String fbSalt, String fbAppId, String fbSecret) {
        FBConfigFactory fbConfigFactory = new FBConfigFactory(fbSalt, fbAppId, Paths.FBCALLBACK, fbSecret);
        Config config = fbConfigFactory.build();
        SecurityFilter securityFilter = new SecurityFilter(config, "FacebookClient");

        before("/loginfb", securityFilter);

        get("/loginfb", FBRouteHandler::handleFacebookLogin);

        CallbackRoute callbackRoute = new CallbackRoute(config);

        get(Paths.FBCALLBACK, callbackRoute);
        post(Paths.FBCALLBACK, callbackRoute);
    }




}
