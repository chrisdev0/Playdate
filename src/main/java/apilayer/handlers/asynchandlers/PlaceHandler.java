package apilayer.handlers.asynchandlers;

import apilayer.handlers.Paths;
import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import spark.Request;
import spark.Response;
import utils.CoordinateHandlerUtil;
import utils.ParserHelpers;
import utils.Utils;

import java.util.Optional;

import static spark.Spark.halt;
import static apilayer.handlers.asynchandlers.SparkHelper.*;

@Slf4j
public class PlaceHandler {

    public static Object handleGetOnePlaceWithoutComments(Request request, Response response) {
        Optional<Place> placeById = getPlaceFromRequest(request);
        if (placeById.isPresent()) {
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                    .create().toJson(placeById.get());
        } else {
            response.status(400);
            return "";
        }
    }

    public static Object handleGetPlaceByLoc(Request request, Response response) {
        try {
            int[] grid = getGridLocationFromRequest(request);
            return new Gson().toJson(PlaceDAO.getInstance().getPlaceByLocation(grid[0], grid[1]));
        } catch (IllegalArgumentException e) {
            log.error("error converting location information", e);
        }
        return "error";
    }

    public static Object handleGetPlaceByName(Request request, Response response) {
        String name = request.queryParams("name");
        String offsetStr = request.queryParams("offset");
        try {
            int offset = Integer.parseInt(offsetStr);
            if (Utils.isNotNullAndNotEmpty(name)) {
                return new Gson().toJson(PlaceDAO.getInstance().getPlacesByName(name, offset, 10));
            }
        } catch (NumberFormatException e) {
            return "error";
        }
        return "error";
    }

    public static Object handleGetPlaceByGeoArea(Request request, Response response) {
        String geoAreaName = request.queryParams("name");
        String offsetStr = request.queryParams("offset");
        try {
            int offset = Integer.parseInt(offsetStr);
            if (Utils.isNotNullAndNotEmpty(geoAreaName)) {
                return new Gson().toJson(PlaceDAO.getInstance().getPlacesByGeoArea(geoAreaName, offset, 10));
            }
        } catch (NumberFormatException e) {
            return "error";
        }
        return "error";
    }
}
