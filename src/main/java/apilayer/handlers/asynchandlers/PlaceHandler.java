package apilayer.handlers.asynchandlers;

import apilayer.handlers.Paths;
import com.google.gson.Gson;
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

@Slf4j
public class PlaceHandler {

    public static Object handleGetOnePlace(Request request, Response response) {
        String idStr = request.queryParams(Paths.QueryParams.PLACE_BY_ID);
        Long id = ParserHelpers.parseToLong(idStr);
        Optional<Place> placeById = PlaceDAO.getInstance().getPlaceById(id);
        if (placeById.isPresent()) {
            return new Gson().toJson(placeById.get());
        } else {
            response.status(400);
            return "";
        }
    }

    public static Object handleGetPlaceByLoc(Request request, Response response) {
        String locXStr = request.queryParams("locX");
        String locYStr = request.queryParams("locY");
        if (Utils.isNotNullAndNotEmpty(locXStr) && Utils.isNotNullAndNotEmpty(locYStr)) {
            CoordinateHandlerUtil coordinateHandlerUtil = new CoordinateHandlerUtil();
            try {
                double locX = Double.parseDouble(locXStr);
                double locY = Double.parseDouble(locYStr);
                double[] grid = coordinateHandlerUtil.geodeticToGrid(locX, locY);
                return new Gson().toJson(PlaceDAO.getInstance().getPlaceByLocation((int)grid[0], (int)grid[1]));
            } catch (NumberFormatException e) {
                log.error("Error parsing loc", e);
            }
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
