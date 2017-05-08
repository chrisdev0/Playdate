package apilayer.route;

import apilayer.StaticFileTemplateHandlerImpl;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.get;

public class StaticFileRoutes {

    public static void initStaticFileRoutes() {
        get("/createplaydate.html",
                new StaticFileTemplateHandlerImpl("TODELETE/createplaydate.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());
        get("/glomtlosenord.html",
                new StaticFileTemplateHandlerImpl("TODELETE/forgotpassword.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());
        get("/showplaydatepage.html",
                new StaticFileTemplateHandlerImpl("TODELETE/showplaydatepage.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());

        get("/changeProfile.html",
                new StaticFileTemplateHandlerImpl("TODELETE/changeProfile.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());

        get("/makeProfile.html",
                new StaticFileTemplateHandlerImpl("TODELETE/makeProfile.vm", 500)::handleTemplateFileRequest,
                new VelocityTemplateEngine());


    }

}
