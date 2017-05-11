package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.CommentHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.Comment;
import model.Place;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;

import java.util.Optional;
import java.util.Set;

@Slf4j
public class CommentsHandler {


    /** Hanterar post av en kommentar på en plats
     *
     *  Vilken plats som får kommentaren beror på vilken id som
     *  skickas in som 'placeId'
     *
     *  Skapar en kommentar och lägger till den hos place och sparar kommentaren
     *  och uppdaterar place
     *
     *  Kommentaren fås fram genom 'comment' och user kan hämtas
     *  ur session hos request-objektet.
     *
     * @param request request för post av kommentaren
     * @param response response for post av kommentaren
     *
     * @return      returnerar JSON med alla kommentarer
     * */
    public static Object handlePostComment(Request request, Response response) {
        Long placeId = ParserHelpers.parseToLong(request.queryParams("placeId"));
        String commentStr = request.queryParams("comment");
        Comment comment = new Comment(commentStr, request.session().attribute(Constants.USER_SESSION_KEY), false);
        Optional<Place> placeOptional = PlaceDAO.getInstance().getPlaceById(placeId);
        if (placeOptional.isPresent()) {
            Optional<Set<Comment>> comments = PlaceDAO.getInstance().saveComment(comment, placeOptional.get());
            if (comments.isPresent()) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                return gson.toJson(comments.get());
            } else {
                response.status(400);
                return "";
            }
        } else {
            log.error("couldn't find place to post comment in");
            response.status(400);
            return "";
        }

    }
}
