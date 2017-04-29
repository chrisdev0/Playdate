package apilayer.route;

import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.Paths;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.get;

public class StaticFileRoutes {

    public static void initStaticFileRoutes() {
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
        get("/showplaydatepage.html",
                new StaticFileTemplateHandlerImpl("showplaydatepage.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());

        get("/changeProfile.html",
                new StaticFileTemplateHandlerImpl("changeProfile.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());

        get("/makeProfile.html",
                new StaticFileTemplateHandlerImpl("makeProfile.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());
    }

}
