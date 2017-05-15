package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.Comment;
import model.Place;
import spark.Request;
import spark.Response;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static apilayer.handlers.asynchandlers.SparkHelper.getPlaceFromRequest;

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
        String commentStr = request.queryParams("comment");
        Optional<Place> placeOptional = getPlaceFromRequest(request);
        if (placeOptional.isPresent()) {
            Comment comment = new Comment(commentStr, request.session().attribute(Constants.USER_SESSION_KEY), false);
            Optional<Set<Comment>> comments = PlaceDAO.getInstance().saveComment(comment, placeOptional.get());
            if (comments.isPresent()) {
                return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(
                        comments.get().stream().sorted().collect(Collectors.toList()));
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

    public static Object handleGetCommentsOfPlace(Request request, Response response) {
        Optional<Place> placeOptional = getPlaceFromRequest(request);
        if (placeOptional.isPresent()) {
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(
                    placeOptional.get().getComments().stream().sorted().collect(Collectors.toList()));
        }
        response.status(400);
        return Constants.MSG.NO_PLACE_WITH_ID;
    }


}
