package apilayer.route;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.Paths;
import apilayer.handlers.adminhandlers.DashboardHandler;
import lombok.extern.slf4j.Slf4j;
import model.User;
import spark.template.velocity.VelocityTemplateEngine;


import static spark.Spark.*;

@Slf4j
public class AdminRoutes {

    public static void initAdminRoutes() {
        path(Paths.ADMIN.ADMIN, () ->{
            before("/*", (request, response) -> {
                if (!((User) request.session().attribute((Constants.USER_SESSION_KEY))).isAdmin()) {
                    throw halt(500);
                }
            });

            get(Paths.ADMIN.ADMIN_INDEX,
                    new DashboardHandler()::handleTemplateFileRequest,
                    new VelocityTemplateEngine());
        });
    }

}
