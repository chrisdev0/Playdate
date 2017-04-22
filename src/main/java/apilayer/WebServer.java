package apilayer;

import apilayer.handlers.GetOnePlaceHandler;
import apilayer.handlers.LoginTryHandler;
import com.google.gson.Gson;
import dblayer.HibernateStarter;
import model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.TemplateEngine;
import spark.route.RouteOverview;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class WebServer {

    private SessionFactory sessionFactory;
    private Logger logger = LoggerFactory.getLogger(WebServer.class);
    private TemplateEngine templateEngine;

    public WebServer() {
        port(Constants.PORT);
        setStaticFilesPath();
        RouteOverview.enableRouteOverview();
        initHibernate();
        initDEVData();
        templateEngine = new VelocityTemplateEngine();
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
        try {
            HibernateStarter hibernateStarter = new HibernateStarter();
            sessionFactory = hibernateStarter.initConfig();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void initDEVData() {
        User user = new User("abc", "Hej Hejsan", "a@b.com", "password","123", "..", Gender.FLICKE);
        Child child = new Child(18, Gender.FLICKE, user);
        user.addChild(child);
        User user2 = new User("abc", "Nils Svensson", "hej@b.com", "password", "1231", "..", Gender.POJKE);
        Place place = new Place("abc-123", "Testlekplats", "Testlekplats ligger aula nod på DSV", "123", "123", 10, 10);
        Comment comment = new Comment("Bästa stället i stockholm", user, false, place);
        Comment comment2 = new Comment("Bättre än L50, sämre än L30. Brukar gå hit med min son Bengt-Fridolf för att lyssna på föreläsningar om UML", user2, false, place);
        place.addComment(comment);
        place.addComment(comment2);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(user);
            session.save(user2);
            session.save(child);
            session.save(place);
            session.save(comment);
            session.save(comment2);
            tx.commit();
        } catch (Exception e) {
            logger.error("hibernate error", e);
        }
    }



    private void initRoutes() {

        get("/index.html",
                new StaticFileTemplateHandlerImpl("index.vm",500)::handleTemplateFileRequest,
                templateEngine);


        path("/protected", () -> {
            before("/*", (request, response) -> {
                if (!isLoggedIn(request)) {
                    logger.info("client " + request.ip() + " isn't logged in. Halting request");
                    throw halt(401);
                }
            });

            get("/getallplaydates", (request, response) -> {
                //todo
                throw halt(400);
            });

            get("/getallplace", ((request, response) -> {
                //todo
                throw halt(400);
            }));

            get("/getoneplace", (request, response) -> {
                //todo
                String id = request.queryParams("placeId");
                try {
                    long lId = Long.parseLong(id);
                    logger.info("fetching place with id = " + lId);
                    return new GetOnePlaceHandler("showplacepage.vm", lId, 400, sessionFactory)
                            .handleTemplateFileRequest(request, response);
                } catch (NullPointerException | NumberFormatException e) {
                    logger.info("client: " + request.ip() + " sent illegal place id = " + id + " e = " + e.getMessage());
                    throw halt(400);
                }
            },templateEngine);

            get("/protectedpage.html", (request, response) -> {
                Map<String, Object> model = new HashMap<>();
                model.put("user", request.session().attribute("user"));
                return new ModelAndView(model, "protectedpage.vm");
            }, templateEngine);
        });

        get("/logout", (request, response) -> {
            request.session().invalidate();
            throw halt(200);
        });



        //Hanterar inloggningsförsök
        post("/trylogin", new LoginTryHandler(sessionFactory)::handle);
    }

    private boolean isLoggedIn(Request request) {
        logger.info("client " + request.ip() + " checking loginstatus = " + request.session().attribute("user"));
        return request.session().attribute("user") != null;
    }

    private void protectedAPI() {
        get("/getusername", (request, response) -> {
            User user = request.session().attribute("user");
            if (user == null) {
                logger.info("user is null and not logged in");
            } else {
                logger.info(user.getName());
            }
            return new Gson().toJson(user);
        });
    }

}
