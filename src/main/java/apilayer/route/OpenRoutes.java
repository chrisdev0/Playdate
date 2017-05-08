package apilayer.route;

import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.fbhandlers.FBConfigFactory;
import apilayer.fbhandlers.FBRouteHandler;
import apilayer.handlers.*;
import cache.Cache;
import com.google.gson.Gson;
import dblayer.PaginationWrapper;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.DBAPIImage;
import model.Place;
import org.pac4j.core.config.Config;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.SecurityFilter;
import presentable.FeedObject;
import secrets.Secrets;
import spark.template.velocity.VelocityTemplateEngine;
import utils.ParserHelpers;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static spark.Spark.*;

@Slf4j
public class OpenRoutes {

    /** Initierar de routes som ska leda till statiska template-filer
     *  så att index.html (och "/") leder till att index.vm blir renderat
     * */
    public static void initOpenRoutes() {

        get("/", new StaticFileTemplateHandlerImpl("index.vm", 400, false)::handleTemplateFileRequest, new VelocityTemplateEngine());
        get("/index.html", new StaticFileTemplateHandlerImpl("index.vm", 400, false)::handleTemplateFileRequest, new VelocityTemplateEngine());


        //hanterar logout
        get(Paths.LOGOUT, LoginHandler::logOut);



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
