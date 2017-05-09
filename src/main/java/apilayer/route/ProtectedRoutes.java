package apilayer.route;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.*;
import apilayer.handlers.asynchandlers.SearchHandlers;
import apilayer.handlers.templateHandlers.GetOnePlaceHandler;
import apilayer.handlers.templateHandlers.GetOnePlaydateHandler;
import apilayer.handlers.templateHandlers.GetOneUserHandler;
import apilayer.handlers.templateHandlers.GetUserPlaydateHandler;
import com.google.gson.Gson;
import dblayer.PaginationWrapper;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import presentable.FeedObject;
import spark.template.velocity.VelocityTemplateEngine;
import utils.ParserHelpers;

import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;
import static spark.Spark.delete;
import static spark.Spark.get;

@Slf4j
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
                if (!AuthChecker.isLoggedIn(request)) {
                    throw halt(401, Constants.MSG.USER_NOT_LOGGED_IN);
                }
            });



            /*  Hanterar add av kommentarer till ett place
            *   place bestäms av "placeId", kommentaren av "comment"
            *   vid success så redirectas användaren tillbaka till sidan för place
            *       todo göra asynkront med automatisk inläggning av kommentaren
            *       todo (också att göra då är att ladda kommentarerna asynkront)
            *   returnerar halt(400) vid error
            * */
            post(Paths.POSTCOMMENT, CommentHandler::handlePostComment);

            post(Paths.CREATEPLAYDATE, PlaydateHandler::handleMakePlaydate);

            get(Paths.GETONEPLAYDATE, new GetOnePlaydateHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());



            //delete(Paths.DELETECOMMENT, CommentHandler::handleRemoveComment);
            delete(Paths.DELETEPLAYDATE, PlaydateHandler::handleDeletePlaydate);

            //ska pathen vara showplaydate?
            put(Paths.SHOWPLAYDATES, PlaydateHandler::removePlaydateAttendance);

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

            get(Paths.SEARCH_PLACE_BY_TERM, SearchHandlers::searchPlaces);

            initProtectedStaticRoutes();

        });
    }

    private static void initProtectedStaticRoutes() {

        get(Paths.LANDING, new StaticFileTemplateHandlerImpl("feed.vm", 400, true)::handleTemplateFileRequest, new VelocityTemplateEngine());

        get(Paths.EDITPROFILE, new StaticFileTemplateHandlerImpl("editprofile.vm", 400, true)::handleTemplateFileRequest, new VelocityTemplateEngine());

        get(Paths.SHOWPLAYDATES, new GetUserPlaydateHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());


        get(Paths.CREATEPLAYDATE,
                new StaticFileTemplateHandlerImpl("createplaydate.vm", 400, true)::handleTemplateFileRequest, new VelocityTemplateEngine());

        get(Paths.SHOWPLACE, new GetOnePlaceHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());


        get(Paths.SHOWUSER, new GetOneUserHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());

        /*
        get(Paths.SHOWPROFILE, new StaticFileTemplateHandlerImpl("TODELETE/show-profile.vm", 400){
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
        */
    }



}
