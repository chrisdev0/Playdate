package apilayer;

import apilayer.handlers.*;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.route.RouteOverview;
import spark.template.velocity.VelocityTemplateEngine;


import static dblayer.DBDataCreator.initDEVData;
import static spark.Spark.*;

@Slf4j
public class WebServer {

    private HibernateUtil hibernateUtil;

    public WebServer() {
        port(Constants.PORT);
        setStaticFilesPath();
        RouteOverview.enableRouteOverview();
        initHibernate();
        initDEVData();
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
        hibernateUtil = HibernateUtil.getInstance();
    }


    /** Initierar routes för applikationen
     *  Går att se vilka som finns (om RouteOverview.enableRouteOverview(); blir anropat innan)
     *  på "/debug/routeoverview/"
     * */
    private void initRoutes() {
        //initierar de routes för statiska filer (templates) som inte är protected
        staticFileRoutes();

        //initierar de routes för statiska filer (templates) och API som är protected
        protectedPaths();

        //Hanterar inloggningsförsök
        post(Paths.TRYLOGIN, new LoginHandler()::handleLoginTry);

        //hanterar logout
        get(Paths.LOGOUT, LoginHandler::logOut);
    }

    /** Initierar de routes som kräver att användaren är inloggad
     *
     *  Kollar först om användaren är inloggad, om användaren inte är inloggad så returneras
     *  halt(401) med ett meddelande
     * */
    private void protectedPaths() {
        path(Paths.PROTECTED, () -> {
            //Kollar att användaren är inloggad innan varje request hanteras
            before("/*", (request, response) -> {
                if (!AuthChecker.isLoggedIn(request, response)) {
                    throw halt(401,Constants.MSG.USER_NOT_LOGGED_IN);
                }
            });

            /*  Hanterar retur av alla playdates
            *       todo bör förmodligen endast returnera en del med ett offset som returnerar fler
            *       todo asykront när användaren scrollar
            * */
            get(Paths.GETALLPLAYDATES, (request, response) -> {
                //todo
                throw halt(400);
            });

            /*  Hanterar retur av alla Place
            *       todo bör förmodligen endast returnera en del med ett offset som returnerar fler
            *       todo asykront när användaren scrollar
            * */
            get(Paths.GETALLPLACE, ((request, response) -> {
                //todo
                throw halt(400);
            }));

            /*  Hanterar retur av en Place
            *   id för place specificeras i ?placeId=<ID:t>
            *       todo returnerar nu halt(400) ifall ingen plats med det ID:t hittas, borde nog ändras
            *       todo någon typ av error-sida visas (som eventuellt är gemensam med andra
            *       todo template-routes som kan returnera error)
            * */
            get(Paths.GETONEPLACE, PlaceHandler::handleGetOnePlace, new VelocityTemplateEngine());

            /*  Hanterar add av kommentarer till ett place
            *   place bestäms av "placeId", kommentaren av "comment"
            *   vid success så redirectas användaren tillbaka till sidan för place
            *       todo göra asynkront med automatisk inläggning av kommentaren
            *       todo (också att göra då är att ladda kommentarerna asynkront)
            *   returnerar halt(400) vid error
            * */
            post(Paths.POSTCOMMENT, CommentHandler::handlePostComment);

            post(Paths.CREATEPLAYDATE, PlaydateHandler::handleMakePlaydate);

            get(Paths.GETONEPLAYDATE, PlaydateHandler::handleGetOnePlaydate, new VelocityTemplateEngine());


            put(Paths.CREATEPROFILE, new ProfileHandler("showProfile.vm",400)::handleTemplateFileRequest, new VelocityTemplateEngine());


        });
    }

    /** Initierar de routes som ska leda till statiska template-filer
     *  så att index.html (och "/") leder till att index.vm blir renderat
     *
     *      todo eventuellt ta bort de resurser som inte behöver någon renderad data och
     *      todo låt de vara statiska .html-filer i /public istället.
     * */
    private void staticFileRoutes() {
        get(Paths.StaticFilePaths.INDEX_HTML,
                new StaticFileTemplateHandlerImpl("index.vm",500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());
        get("/",
                new StaticFileTemplateHandlerImpl("index.vm",500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());
        get("/registerform.html",
                new StaticFileTemplateHandlerImpl("registerform.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());
        get("/createplaydate.html",
                new StaticFileTemplateHandlerImpl("createplaydate.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());
        get("/glomtlosenord.html",
                new StaticFileTemplateHandlerImpl("forgotpassword.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());
    }
}
