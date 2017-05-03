package apilayer.handlers;

import apilayer.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import dblayer.HibernateUtil;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import org.hibernate.Session;
import org.hibernate.query.Query;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import utils.CoordinateHandlerUtil;
import utils.Utils;

import java.util.List;

import static spark.Spark.halt;

@Slf4j
public class PlaceHandler {

    public static ModelAndView handleGetOnePlace(Request request, Response response) {
        String id = request.queryParams(Paths.QueryParams.GET_ONE_PLACE_BY_ID);
        try {
            long lId = Long.parseLong(id);
            log.info("fetching place with id = " + lId);
            return new GetOnePlaceHandler("showplacepage.vm", lId, 400)
                    .handleTemplateFileRequest(request, response);
        } catch (NullPointerException | NumberFormatException e) {
            log.info("client: " + request.ip() + " sent illegal place id = " + id + " e = " + e.getMessage());
            throw halt(400);
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
        if (Utils.isNotNullAndNotEmpty(name)) {
            return PlaceDAO.getInstance().getPlacesByName(name);
        }
        return "error";
    }
}
