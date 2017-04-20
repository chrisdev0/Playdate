package apilayer;

import apilayer.handlers.LoginTryHandler;
import dblayer.HibernateStarter;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import spark.staticfiles.StaticFilesConfiguration;

import static spark.Spark.*;

public class WebServer {

    private StaticFilesConfiguration staticHandler;
    private SessionFactory sessionFactory;
    private static boolean first = true;

    public WebServer() {
        port(Constants.PORT);
        staticHandler = new StaticFilesConfiguration();
//        if (Constants.LOCALHOST) {
//            String projectDir = System.getProperty("user.dir");
//            String staticDir = "/src/main/resources/public";
//            staticHandler.configureExternal(projectDir + staticDir);
//        } else {
//            staticHandler.configure("/public");
//        }
        staticFileLocation("/public");
        if (Constants.LOCALHOST) {
            String projectDir = System.getProperty("user.dir");
            String staticDir = "/src/main/resources/public";
            staticHandler.configureExternal(projectDir + staticDir);
        } else {
            staticHandler.configure("/public");
        }
        //staticFileLocation("/public");
        initHibernate();
        initDEVData();
        initRouteHandlers();
        initRoutes();
    }

    private void initHibernate() {
        try {
            HibernateStarter hibernateStarter = new HibernateStarter();
            sessionFactory = hibernateStarter.initConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initDEVData() {
        User user = new User("abc", "Hej Hejsan", "a@b.com", "password", "..");
        User user2 = new User("abc", "Hej Hejsan", "hej@b.com", "password", "..");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(user);
            session.save(user2);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private FBHandler fbHandler;

    private void initRouteHandlers() {
        fbHandler = new FBHandler();
    }

    private void initRoutes() {
        get("/a", (request, response) -> "hej");

        //Hanterar inloggningsförsök
        post("/trylogin", new LoginTryHandler(sessionFactory)::handle);
    }

}
