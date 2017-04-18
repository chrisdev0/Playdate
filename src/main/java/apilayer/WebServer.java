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

    private void initRoutes() {

    }

    private void initRouteHandlers() {
        get("/a", (request, response) -> "hej");
    }

}
