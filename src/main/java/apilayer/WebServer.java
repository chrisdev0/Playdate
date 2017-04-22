package apilayer;

import apilayer.handlers.LoginTryHandler;
import com.google.gson.Gson;
import dblayer.HibernateStarter;
import model.Child;
import model.Gender;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.route.RouteOverview;
import spark.staticfiles.StaticFilesConfiguration;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class WebServer {

    private SessionFactory sessionFactory;
    private Logger logger = LoggerFactory.getLogger(WebServer.class);

    public WebServer() {
        port(Constants.PORT);
        setStaticFilesPath();
        RouteOverview.enableRouteOverview();
        initHibernate();
        initDEVData();
        initRoutes();
    }

    private void setStaticFilesPath() {
        if (Constants.LOCALHOST) {
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
        User user2 = new User("abc", "Hej Hejsan", "hej@b.com", "password", "1231", "..", Gender.POJKE);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(user);
            session.save(user2);
            session.save(child);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void initRoutes() {
        get("/index.html", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "index.vm");
        }, new VelocityTemplateEngine());

        get("/protectedpage.html", (request, response) -> {
            if (!isLoggedIn(request)) {
                return new ModelAndView(new HashMap<String,Object>(), "notloggedin.vm");
            }
            Map<String, Object> model = new HashMap<>();
            model.put("user", ((User) request.session().attribute("user")));
            return new ModelAndView(model, "protectedpage.vm");
        }, new VelocityTemplateEngine());



        //Hanterar inloggningsförsök
        post("/trylogin", new LoginTryHandler(sessionFactory)::handle);
    }

    private boolean isLoggedIn(Request request) {
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
