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


import java.util.Optional;

import static dblayer.DBDataCreator.initDEVData;
import static spark.Spark.*;

@Slf4j
public class WebServer {

    private static final boolean SHOULD_LOAD_PLACES = false;

    public WebServer() {
        Secrets secrets = Secrets.getInstance();

        Optional<String> serverPort = secrets.getValue("serverPort");
        if (serverPort.isPresent()) {
            log.info("using port from secrets");
            port(Integer.parseInt(serverPort.get()));
        } else {
            log.info("using port 9000");
            port(9000);
        }
        initHTTPS();
        setStaticFilesPath();
        RouteOverview.enableRouteOverview();
        initHibernate();
        Optional<String> stockholmAPIKEYopt = Secrets.getInstance().getValue("stockholmAPIKEY");
        if (stockholmAPIKEYopt.isPresent() && SHOULD_LOAD_PLACES) {
            try {
                new APILoader().doLoadOnStartup(stockholmAPIKEYopt.get(), APIUtils.URLS.LEKPLATSER);
                initDEVData();
            } catch (Exception e) {
                log.error("error loading places ", e);
            }
        }
        initRoutes();
    }

    private void initHTTPS() {
        if (Secrets.getInstance().getValue("ssl").isPresent() && Secrets.getInstance().getValue("ssl").get().equals("yes")) {
            try {
                secure("keystore.jks", Secrets.getInstance().getValue("sslPass").get(), null, null);
            } catch (Exception e) {
                log.error("error creating secure");
                log.error("",e);
            }
        }
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
