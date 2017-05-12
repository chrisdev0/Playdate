package apilayer.route;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.*;
import apilayer.handlers.asynchandlers.*;
import apilayer.handlers.templateHandlers.*;
import com.google.gson.Gson;
import dblayer.PaginationWrapper;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import presentable.FeedObject;
import spark.Request;
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
     *  Kollar först om användaren är inloggad, om användaren inte är inloggad så
     *  Skickas användaren tillbaka till startsidan
     *  om route som leder till att användaren ska redirectas till
     *  startsidan inte är av typen som hämtar en profilbild eller en platsbild från
     *  databasen så sparas den plats användaren var på i en session
     *  när användaren sedan loggat in så hämtas innehållet från session
     *  och användaren redirectas till sidan användaren var på tidigare
     *
     * */
    public static void initProtectedRoutes() {
        path(Paths.PROTECTED, () -> {
            //Kollar att användaren är inloggad innan varje request hanteras
            before("/*", (request, response) -> {
                if (!isLoggedIn(request)) {
                    if (shouldSaveContextPath(request.pathInfo())) {
                        String fullPath = request.pathInfo() + "?" + (request.queryString() != null && !request.queryString().isEmpty() ? request.queryString() : "");
                        request.session(true).attribute(Constants.ONLOGINREDIRECT, fullPath);
                    }
                    response.redirect("/index.html");
                    throw halt(400);
                }
            });



            post(Paths.POSTCOMMENT, CommentsHandler::handlePostComment);

            post(Paths.CREATEPLAYDATE, PlaydateHandler::handleMakePlaydate);



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

            post(Paths.UPDATEPLAYDATE, PlaydateHandler::handleUpdatePlaydate);



            //todo ändra till post med ajax
            post(Paths.ACCEPTFRIENDSHIPREQUEST, FriendsHandler::handleAcceptFriendRequest);

            post(Paths.DECLINEFRIENDSHIPREQUEST, FriendsHandler::handleDeclineFriendshipRequest);

            post(Paths.ACCEPTINVITE, AttendanceInviteHandler::handleAcceptInviteToPlaydate);

            delete(Paths.DECLINEINVITE, AttendanceInviteHandler::handleDeclineInviteToPlaydate);

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

        get(Paths.GETMYFRIENDS, new GetMyFriendsHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());

        get(Paths.GETMYINVITES, new GetMyInvitesHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());

        get(Paths.GETONEPLAYDATE, new GetOnePlaydateHandler()::handleTemplateFileRequest, new VelocityTemplateEngine());



    }

    /** Metoden returnerar true om användaren är inloggad
     *  loggar användarens ip på info
     * @param request request-objektet där session kan hämtas ifrån.
     * @return om användaren är inloggad
     * */
    private static boolean isLoggedIn(Request request) {
        return request.session().attribute(Constants.USER_SESSION_KEY) != null;
    }

}
