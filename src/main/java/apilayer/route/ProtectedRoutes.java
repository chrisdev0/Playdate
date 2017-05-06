package apilayer.route;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.*;
import com.google.gson.Gson;
import dblayer.PaginationWrapper;
import dblayer.PlaceDAO;
import model.Place;
import model.User;
import presentable.FeedObject;
import spark.ModelAndView;
import spark.Request;
import spark.template.velocity.VelocityTemplateEngine;
import utils.ParserHelpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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



            //delete(Paths.DELETECOMMENT, CommentHandler::handleRemoveComment);
            delete(Paths.DELETEPLAYDATE, PlaydateHandler::handleDeletePlaydate);

            //ska pathen vara showplaydate?
            put(Paths.SHOWPLAYDATE, PlaydateHandler::removePlaydateAttendance);

            get("/landingpage", (request, response) -> {
                User user = request.session().attribute(Constants.USER_SESSION_KEY);
                Map<String, Object> map = new HashMap<>();
                map.put("user", user);
                return new ModelAndView(map, "landingpage.vm");
            }, new VelocityTemplateEngine());

            get(Paths.APIIMAGE + "/:id", ImageHandler::handleGetAPIImage);

            get(Paths.GETPLACEBYLOCATION, PlaceHandler::handleGetPlaceByLoc);

            get(Paths.GETPLACEBYNAME, PlaceHandler::handleGetPlaceByName);

            get(Paths.GETPLACEBYGEONAME, PlaceHandler::handleGetPlaceByGeoArea);

            post(Paths.POSTNEWPROFILEPICTURE, UploadHandler::handleUploadProfilePicture);

            get(Paths.GETPROFILEPICTURE + "/:id", ImageHandler::handleGetProfilePicture);

            get(Paths.GETFEED, (request, response) -> {
                Optional<PaginationWrapper<Place>> norrmalm = PlaceDAO.getInstance().getPlacesByGeoArea("Norrmalm", ParserHelpers.parseToInt(request.queryParams("offset")), 10);
                PaginationWrapper<FeedObject> paginationWrapper = new PaginationWrapper<>(
                        norrmalm.get().getCollection().stream().map(FeedObject::createFromPlace).collect(Collectors.toList()),
                        norrmalm.get().getPaginationOffset());
                return new Gson().toJson(paginationWrapper);
            });

            initProtectedStaticRoutes();

        });
    }

    private static void initProtectedStaticRoutes() {

        get(Paths.LANDING, new StaticFileTemplateHandlerImpl("landingpage.vm", 400)::handleTemplateFileRequest, new VelocityTemplateEngine());
        get(Paths.SHOWPROFILE, new StaticFileTemplateHandlerImpl("show-profile.vm", 400){
            @Override
            public Optional<Map<String, Object>> createModelMap(Request request) {
                Map<String, Object> map = new HashMap<>();
                User user = request.session().attribute(Constants.USER_SESSION_KEY);
                user.setEmail(user.getEmail().toLowerCase());
                map.put("gender", user.getGender());
                map.put("user", user);
                return Optional.of(map);
            }
        }::handleTemplateFileRequest, new VelocityTemplateEngine());

    }

}
