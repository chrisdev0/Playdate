package apilayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class WebServer {


    public WebServer() {
        port(Constants.PORT);
        if (Constants.LOCALHOST) {
            String projectDir = System.getProperty("user.dir");
            String staticDir = "/src/main/resources/public";
            staticFiles.externalLocation(projectDir + staticDir);
        } else {
            staticFiles.location("/public");
        }
        initRouteHandlers();
        initRoutes();
    }

    private FBHandler fbHandler;

    private void initRoutes() {
        get("/a", (request, response) -> "hej");
        post("/fblogin", fbHandler.getFbInfoRequestReceiver()::handle);
        get("/redirect", (request, response) -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.info("Request body on redirect " + request.body());
            return request.body();
        });
    }

    private void initRouteHandlers() {
        fbHandler = new FBHandler();
    }

}
