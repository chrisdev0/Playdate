package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dblayer.PlaydateDAO;
import dblayer.SearchDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import spark.Request;
import spark.Response;
import utils.CoordinateHandlerUtil;
import utils.ParserHelpers;
import utils.filters.TimeFilterable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static apilayer.handlers.asynchandlers.SparkHelper.*;

@Slf4j
public class SearchHandlers {


    /** Hanterar för sök efter platser på namn på
     *      * platsen
     *      * området
     *      * adressen
     *      m.m.?
     *
     * returnerar ett antal Place som JSON
     * om antalet bokstäver i sökfältet är mindre än 3 så returneras inga resultat
     *
     * resultatet av en sökning cacheas
     * */
    public static Object searchPlaces(Request request, Response response) {
        String term = request.queryParams("searchTerm");
        if (term.length() < 3) {
            return "";
        }
        try {
            List<Place> placeByTermThroughCache = SearchDAO.getInstance().getPlaceByTermThroughCache(term);
            placeByTermThroughCache.forEach(place -> place.setComments(null));
            return new Gson().toJson(placeByTermThroughCache);
        } catch (ExecutionException e) {
            log.error("error searching by term through cache", e);
            response.status(500);
            return "";
        }
    }

    public static Object searchPublicPlaydatesByLoc(Request request, Response response) {
        double geoXstr = ParserHelpers.parseToDouble(request.queryParams(Paths.QueryParams.LOC_X));
        double geoYstr = ParserHelpers.parseToDouble(request.queryParams(Paths.QueryParams.LOC_Y));
        double[] grids = new CoordinateHandlerUtil().geodeticToGrid(geoXstr, geoYstr);
        Optional<List<Playdate>> playdatesOpt = PlaydateDAO.getInstance().getPublicPlaydatesByLoc((int)grids[0], (int)grids[1], TimeFilterable.TimeFilter.ALL);
        return playdatesOpt.map(playdates -> new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(playdates)).orElse((String) setStatusCodeAndReturnString(response, 400, Constants.MSG.ERROR));
    }


    public static Object searchPublicPlaydatesByMultiPlace(Request request, Response response) {
        String[] multiPlaceIds = request.queryParams(Paths.QueryParams.MULTI_PLACE_IDS).split(",");
        List<Long> placeIds = Stream.of(multiPlaceIds).map(ParserHelpers::parseToLong).collect(Collectors.toList());
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                .toJson(PlaydateDAO.getInstance().getPlaydatesOfMultiplePlace(placeIds, TimeFilterable.TimeFilter.ALL));
    }

}
