package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import dblayer.PlaceDAO;
import dblayer.PlaydateDAO;
import model.Place;
import model.Playdate;
import model.User;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;

import java.util.Optional;

public class SparkHelper {


    static Optional<Place> getPlaceFromRequest(Request request) {
        Long placeId = ParserHelpers.parseToLong(request.queryParams(Paths.QueryParams.PLACE_BY_ID));
        return PlaceDAO.getInstance().getPlaceById(placeId);
    }

    static Optional<Playdate> getPlaydateFromRequest(Request request) {
        Long playdateId = ParserHelpers.parseToLong(request.queryParams(Paths.QueryParams.PLAYDATE_BY_ID));
        return PlaydateDAO.getInstance().getPlaydateById(playdateId);
    }

    static User getUserFromSession(Request request) {
        return request.session().attribute(Constants.USER_SESSION_KEY);
    }

    static Object setStatusCodeAndReturnString(Response response, int statusCode, String returnValue) {
        response.status(statusCode);
        return returnValue == null ? "" : returnValue;
    }

}
