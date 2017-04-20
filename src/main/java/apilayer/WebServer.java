package apilayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.staticfiles.StaticFilesConfiguration;

import static spark.Spark.*;

public class WebServer {

    private StaticFilesConfiguration staticHandler;
    private static boolean first = true;

    public WebServer() {
        port(Constants.PORT);
        staticHandler = new StaticFilesConfiguration();
        if (Constants.LOCALHOST) {
            String projectDir = System.getProperty("user.dir");
            String staticDir = "/src/main/resources/public";
            staticHandler.configureExternal(projectDir + staticDir);
        } else {
            staticHandler.configure("/public");
        }
        //staticFileLocation("/public");
        initRouteHandlers();
        initRoutes();
    }

    private FBHandler fbHandler;

    private void initRouteHandlers() {
        fbHandler = new FBHandler();
    }

    private void initRoutes() {

        before((request, response) -> {
            if (first) {
                first = false;
                halt(401);
            }
            staticHandler.consume(request.raw(), response.raw());
        });

        get("/a", (request, response) -> "hej");


    }

}
