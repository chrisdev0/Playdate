package apilayer;

import apilayer.route.OpenRoutes;
import apilayer.route.ProtectedRoutes;
import apilayer.route.StaticFileRoutes;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import secrets.Secrets;
import spark.route.RouteOverview;
import stockholmapi.APILoader;
import stockholmapi.helpers.APIUtils;


import static spark.Spark.*;

@Slf4j
public class WebServer {


    public WebServer() {
        port(getHerokuAssignedPort());
        initHTTPS();
        setStaticFilesPath();
        RouteOverview.enableRouteOverview();
        initHibernate();
        initRoutes();
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            Constants.DEV = false;
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return Secrets.PORT; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    private void initHTTPS() {
        if (Secrets.USE_SSL) {
            try {
                secure("keystore.jks", Secrets.KEYSTORE_PASSWORD, null, null);
            } catch (Exception e) {
                log.error("error creating secure", e);
            }
        }
    }

    /** Sätter vart statiska filer ska hämtas ifrån
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
