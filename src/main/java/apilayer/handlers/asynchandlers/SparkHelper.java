package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import dblayer.PlaceDAO;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import model.User;
import spark.Request;
import spark.Response;
import utils.CoordinateHandlerUtil;
import utils.ParserHelpers;
import utils.Utils;

import java.util.Optional;

import static utils.ParserHelpers.*;

@Slf4j
public class SparkHelper {


    public static Optional<Place> getPlaceFromRequest(Request request) {
        Long placeId = parseToLong(request.queryParams(Paths.QueryParams.PLACE_BY_ID));
        return PlaceDAO.getInstance().getPlaceById(placeId);
    }

    public static Optional<Playdate> getPlaydateFromRequest(Request request) {
        Long playdateId = parseToLong(request.queryParams(Paths.QueryParams.PLAYDATE_BY_ID));
        return PlaydateDAO.getInstance().getPlaydateById(playdateId);
    }

    public static Optional<User> getUserFromRequestById(Request request) {
        return UserDAO.getInstance().getUserById(parseToLong(request.queryParams(Paths.QueryParams.USER_BY_ID)));
    }

    public static User getUserFromSession(Request request) {
        return request.session().attribute(Constants.USER_SESSION_KEY);
    }

    public static int getOffsetFromRequest(Request request) {
        return ParserHelpers.parseToInt(request.queryParams(Paths.QueryParams.OFFSET));
    }

    public static int[] getGridLocationFromRequest(Request request) throws IllegalArgumentException {
        try {
            CoordinateHandlerUtil coordinateHandlerUtil = new CoordinateHandlerUtil();
            double locX = Double.parseDouble(request.queryParams(Paths.QueryParams.LOC_X));
            double locY = Double.parseDouble(request.queryParams(Paths.QueryParams.LOC_Y));
            double[] gridD = coordinateHandlerUtil.geodeticToGrid(locX, locY);
            return coordinateHandlerUtil.doubleToInt(gridD);
        } catch (Exception e) {
            log.error("error converting latlng to grid", e);
            throw new IllegalArgumentException();
        }
    }

    public static Object setStatusCodeAndReturnString(Response response, int statusCode, String returnValue) {
        response.status(statusCode);
        return returnValue == null ? "" : returnValue;
    }

}
