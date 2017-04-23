package apilayer;

import apilayer.handlers.*;
import com.google.gson.Gson;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;
import spark.route.RouteOverview;
import spark.template.velocity.VelocityTemplateEngine;


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

    private void setStaticFilesPath() {
        if (Constants.DEV) {
            String projectDir = System.getProperty("user.dir");
            String staticDir = "/src/main/resources/public";
            staticFiles.externalLocation(projectDir + staticDir);
        } else {
            staticFiles.location("/public");
        }
    }

    private void initHibernate() {
        hibernateUtil = HibernateUtil.getInstance();
    }

    private void initDEVData() {
        User user = new User("abc", "Hej Hejsan", "a@b.com", "password","123", "..", Gender.FEMALE);
        Child child = new Child(18, Gender.FEMALE, user);
        user.addChild(child);
        User user2 = new User("abc", "Nils Svensson", "hej@b.com", "password", "1231", "..", Gender.MALE);
        Place place = new Place("abc-123", "Testlekplats", "Testlekplats ligger i aula nod på DSV", "images/testlekplats.png", "123", "123", 10, 10);
        Comment comment = new Comment("Bästa stället i stockholm", user, false);
        Comment comment2 = new Comment("Bättre än L50, sämre än L30. Brukar gå hit med min son Bengt-Fridolf för att lyssna på föreläsningar om UML", user2, false);
        place.addComment(comment);
        place.addComment(comment2);
        try (Session session = hibernateUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(user);
            session.save(user2);
            session.save(child);
            session.save(place);
            session.save(comment);
            session.save(comment2);
            tx.commit();
        } catch (Exception e) {
            log.error("hibernate error", e);
        }
    }



    private void initRoutes() {
        staticFileRoutes();

        protectedPaths();

        //Hanterar inloggningsförsök
        post(Paths.TRYLOGIN, new LoginTryHandler()::handle);
    }

    private void protectedPaths() {
        path(Paths.PROTECTED, () -> {
            before("/*", (request, response) -> {
                if (AuthChecker.isLoggedIn(request, response)) {
                    throw halt(401,Constants.MSG.USER_NOT_LOGGED_IN);
                }
            });

            get(Paths.GETALLPLAYDATES, (request, response) -> {
                //todo
                throw halt(400);
            });

            get(Paths.GETALLPLACE, ((request, response) -> {
                //todo
                throw halt(400);
            }));

            get(Paths.GETONEPLACE, PlaceHandler::handleGetOnePlace ,new VelocityTemplateEngine());

            post(Paths.POSTCOMMENT, CommentHandler::handlePostComment);

        });

        get(Paths.LOGOUT, (request, response) -> {
            request.session().invalidate();
            throw halt(200);
        });
    }

    private void staticFileRoutes() {
        get(Paths.StaticFilePaths.INDEX_HTML,
                new StaticFileTemplateHandlerImpl("index.vm",500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());
        get("/",
                new StaticFileTemplateHandlerImpl("index.vm",500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());
    }

    private boolean isLoggedIn(Request request) {

        return request.session().attribute("user") != null;
    }

    private void protectedAPI() {
        get("/getusername", (request, response) -> {
            User user = request.session().attribute("user");
            if (user == null) {
                log.info("user is null and not logged in");
            } else {
                log.info(user.getName());
            }
            return new Gson().toJson(user);
        });
    }

}
