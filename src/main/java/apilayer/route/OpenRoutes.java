package apilayer.route;

import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.fbhandlers.FBConfigFactory;
import apilayer.fbhandlers.FBRouteHandler;
import apilayer.handlers.*;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.config.Config;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.SecurityFilter;
import secrets.Secrets;
import spark.template.velocity.VelocityTemplateEngine;

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



        initFacebookRoutes();
    }

    /** Initierar routes för facebooklogin
     *
     * */
    public static void initFacebookRoutes() {
        FBConfigFactory fbConfigFactory = new FBConfigFactory(Secrets.FB_SALT, Secrets.FB_APP_ID, Secrets.FB_CALLBACK, Secrets.FB_SECRET);
        Config config = fbConfigFactory.build();
        SecurityFilter securityFilter = new SecurityFilter(config, "FacebookClient");

        before("/loginfb", securityFilter);

        get("/loginfb", FBRouteHandler::handleFacebookLogin);

        CallbackRoute callbackRoute = new CallbackRoute(config);

        get(Paths.FBCALLBACK, callbackRoute);
        post(Paths.FBCALLBACK, callbackRoute);
    }




}
