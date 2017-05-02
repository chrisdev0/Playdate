package apilayer;

import apilayer.fbhandlers.FBConfigFactory;
import apilayer.handlers.*;
import apilayer.route.OpenRoutes;
import apilayer.route.ProtectedRoutes;
import apilayer.route.StaticFileRoutes;
import cache.Cache;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;
import secrets.Secrets;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.route.RouteOverview;
import spark.template.velocity.VelocityTemplateEngine;
import stockholmapi.APILoaderOnStartup;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static dblayer.DBDataCreator.initDEVData;
import static spark.Spark.*;

@Slf4j
public class WebServer {

    private static final boolean SHOULD_LOAD_PLACES = true;

    public WebServer() {
        port(Constants.PORT);
        setStaticFilesPath();
        RouteOverview.enableRouteOverview();
        initHibernate();
        Optional<String> stockholmAPIKEYopt = Secrets.getInstance().getValue("stockholmAPIKEY");
        if (stockholmAPIKEYopt.isPresent() && SHOULD_LOAD_PLACES) {
            try {
                new APILoaderOnStartup().doLoad(stockholmAPIKEYopt.get());
                initDEVData();
            } catch (Exception e) {
                log.error("error loading places ", e);
            }
        }
        initRoutes();
    }

    /** Sätter vart statiska filer ska hämtas ifrån
     *  todo    måste förmodligen köra på "/public" när vi bygger Place med hjälp av Stockholms API
     *  todo    om vi kör på att skapa statiska resurser för Place bilder istället för att köra på
     *  todo    typ BLOB i databasen. Fungerar heller inte för velocity-template-filerna
     * */
    private void setStaticFilesPath() {
        if (Constants.DEV) {
            String projectDir = System.getProperty("user.dir");
            String staticDir = "/src/main/resources/public";
            staticFiles.externalLocation(projectDir + staticDir);
        } else {
            staticFiles.location("/public");
        }
    }

    /** Skapar hibernate.
     *  Om skapandet misslyckas av någon anledning så stänger applikationen ner sig
     * */
    private void initHibernate() {
        HibernateUtil hibernateUtil = HibernateUtil.getInstance();
    }


    /** Initierar routes för applikationen
     *  Går att se vilka som finns (om RouteOverview.enableRouteOverview(); blir anropat innan)
     *  på "/debug/routeoverview/"
     * */
    private void initRoutes() {
        //initierar de routes för statiska filer (templates) som inte är protected
        StaticFileRoutes.initStaticFileRoutes();

        //initierar de routes för statiska filer (templates) och API som är protected
        ProtectedRoutes.initProtectedRoutes();

        //initierar de routes för REST som inte kräver att användarne är inloggad
        OpenRoutes.initOpenRoutes();
    }









}
