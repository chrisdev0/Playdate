package apilayer.route;

import apilayer.Constants;
import apilayer.handlers.*;
import model.User;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;
import static spark.Spark.delete;
import static spark.Spark.get;

public class ProtectedRoutes {


    /** Initierar de routes som kräver att användaren är inloggad
     *
     *  Kollar först om användaren är inloggad, om användaren inte är inloggad så returneras
     *  halt(401) med ett meddelande
     * */
    public static void initProtectedRoutes() {
        path(Paths.PROTECTED, () -> {
            //Kollar att användaren är inloggad innan varje request hanteras
            before("/*", (request, response) -> {
                if (!AuthChecker.isLoggedIn(request, response)) {
                    throw halt(401, Constants.MSG.USER_NOT_LOGGED_IN);
                }
            });

            /*  Hanterar retur av alla playdates
            *       todo bör förmodligen endast returnera en del med ett offset som returnerar fler
            *       todo asykront när användaren scrollar
            * */
            get(Paths.GETALLPLAYDATES, (request, response) -> {
                //todo
                throw halt(400);
            });

            /*  Hanterar retur av alla Place
            *       todo bör förmodligen endast returnera en del med ett offset som returnerar fler
            *       todo asykront när användaren scrollar
            * */
            get(Paths.GETALLPLACE, ((request, response) -> {
                //todo
                throw halt(400);
            }));

            /*  Hanterar retur av en Place
            *   id för place specificeras i ?placeId=<ID:t>
            *       todo returnerar nu halt(400) ifall ingen plats med det ID:t hittas, borde nog ändras
            *       todo någon typ av error-sida visas (som eventuellt är gemensam med andra
            *       todo template-routes som kan returnera error)
            * */
            get(Paths.GETONEPLACE, PlaceHandler::handleGetOnePlace, new VelocityTemplateEngine());

            /*  Hanterar add av kommentarer till ett place
            *   place bestäms av "placeId", kommentaren av "comment"
            *   vid success så redirectas användaren tillbaka till sidan för place
            *       todo göra asynkront med automatisk inläggning av kommentaren
            *       todo (också att göra då är att ladda kommentarerna asynkront)
            *   returnerar halt(400) vid error
            * */
            post(Paths.POSTCOMMENT, CommentHandler::handlePostComment);

            post(Paths.CREATEPLAYDATE, PlaydateHandler::handleMakePlaydate);

            get(Paths.GETONEPLAYDATE, PlaydateHandler::handleGetOnePlaydate, new VelocityTemplateEngine());

            get(Paths.SHOWPROFILE,
                    new ShowProfileHandler("showProfile.vm",500)::handleTemplateFileRequest,
                    new VelocityTemplateEngine());

            put(Paths.CREATEPROFILE, new ProfileHandler("showProfile.vm",400)::handleTemplateFileRequest, new VelocityTemplateEngine());

            //delete(Paths.DELETECOMMENT, CommentHandler::handleRemoveComment);
            delete(Paths.DELETEPLAYDATE, PlaydateHandler::handleDeletePlaydate);



            get("/landingpage", (request, response) -> {
                User user = request.session().attribute(Constants.USER_SESSION_KEY);
                Map<String, Object> map = new HashMap<>();
                map.put("user", user);
                return new ModelAndView(map, "landingpage.vm");
            }, new VelocityTemplateEngine());

        });
    }

}
