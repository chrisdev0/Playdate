package apilayer.route;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.*;
import apilayer.handlers.asynchandlers.ProfileHandlers;
import apilayer.handlers.asynchandlers.SearchHandlers;
import apilayer.handlers.asynchandlers.UploadHandler;
import apilayer.handlers.templateHandlers.*;
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
                    if (shouldSaveContextPath(request.pathInfo())) {
                        log.info("queryparams" + request.queryString());
                        log.info("setting onloginredirect route = " + request.pathInfo());
                        String fullPath = request.pathInfo() + "?" + (request.queryString() != null && !request.queryString().isEmpty() ? request.queryString() : "");
                        log.info("full = " + fullPath);
                        request.session(true).attribute(Constants.ONLOGINREDIRECT, fullPath);
                    }
                    response.redirect("/index.html");
                    throw halt(400);
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

            post(Paths.EDITPROFILE, ProfileHandlers::handleEditProfile);



            initProtectedStaticRoutes();

        });
    }

    private static boolean shouldSaveContextPath(String s) {
        return !s.contains(Paths.APIIMAGE) && !s.contains(Paths.GETPROFILEPICTURE);
    }

    private static void initProtectedStaticRoutes() {

        get(Paths.LANDING, new StaticFileTemplateHandlerImpl("feed.vm", 400, true)::handleTemplateFileRequest, new VelocityTemplateEngine());

        get(Paths.EDITPROFILE, new StaticFileTemplateHandlerImpl("editprofile.vm", 400, true)::handleTemplateFileRequest, new VelocityTemplateEngine());

        get(Paths.SHOWPLAYDATES, new GetUserPlaydateHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());


        get(Paths.CREATEPLAYDATE,
                new StaticFileTemplateHandlerImpl("createplaydate.vm", 400, true)::handleTemplateFileRequest, new VelocityTemplateEngine());

        get(Paths.SHOWPLACE, new GetOnePlaceHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());


        get(Paths.SHOWUSER, new GetOneUserHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());

        get(Paths.EDITPLAYDATE, new EditPlaydateHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());


    }



}
