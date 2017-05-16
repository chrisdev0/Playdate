package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.tools.internal.jxc.ap.Const;
import dblayer.PlaceDAO;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.Comment;
import model.Place;
import org.apache.commons.lang.StringEscapeUtils;
import model.Playdate;
import spark.Request;
import spark.Response;
import utils.Utils;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static apilayer.handlers.asynchandlers.SparkHelper.*;

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
    public static Object handlePostPlaceComment(Request request, Response response) {
        String commentStr = request.queryParams("comment");

        if (!Utils.validateLengthOfString(Constants.LONGDESCMIN, Constants.LONGDESCMAX, commentStr)) {
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.VALIDATION_ERROR);
        }

        Optional<Place> placeOptional = getPlaceFromRequest(request);
        if (placeOptional.isPresent()) {
            Comment comment = new Comment(commentStr, getUserFromSession(request), false);
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

    public static Object handlePostPlaydateComment(Request request, Response response) {
        String commentStr = request.queryParams("comment");

        if (!Utils.validateLengthOfString(Constants.LONGDESCMIN, Constants.LONGDESCMAX, commentStr)){
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.VALIDATION_ERROR);
        }

        Optional<Playdate> playdateOptional = getPlaydateFromRequest(request);
        if (playdateOptional.isPresent()) {
            Comment comment = new Comment(commentStr, request.session().attribute(Constants.USER_SESSION_KEY), false);
            Optional<Set<Comment>> comments = PlaydateDAO.getInstance().savePlaydateComment(comment, playdateOptional.get());
            if (comments.isPresent()) {
                return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(
                        comments.get().stream().sorted().collect(Collectors.toList()));
            } else {
                response.status(400);
                return "";
            }
        } else {
            log.error("couldn't find playdate to post comment in");
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

    public static Object handleGetCommentsOfPlaydate(Request request, Response response) {
        Optional<Playdate> playdateOptional = getPlaydateFromRequest(request);
        if (playdateOptional.isPresent()) {
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(
                    playdateOptional.get().getComments().stream().sorted().collect(Collectors.toList()));
        }
        response.status(400);
        return Constants.MSG.NO_PLACE_WITH_ID;
    }



}
