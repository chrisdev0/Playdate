package apilayer.handlers.asynchandlers;

import dblayer.PlaceDAO;
import dblayer.PlaydateDAO;
import model.Place;
import model.Playdate;
import model.User;
import presentable.FeedObject;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static apilayer.handlers.asynchandlers.SparkHelper.*;

public class FeedHandler {

    public static Object handleGetFeed(Request request, Response response) {
        User user = getUserFromSession(request);
        int[] grid = getGridLocationFromRequest(request);
        Optional<List<Playdate>> publicPlaydatesByLoc = PlaydateDAO.getInstance().getPublicPlaydatesByLoc(grid[0], grid[1]);
        List<FeedObject> feedObjects = new ArrayList<>();
        publicPlaydatesByLoc.ifPresent(playdates -> feedObjects.addAll(playdates.stream().map(FeedObject::createFromPlayDate).collect(Collectors.toList())));
        List<Place> placeByLocation = PlaceDAO.getInstance().getPlaceByLocation(grid[0], grid[1]);
        feedObjects.addAll(placeByLocation.stream().map(FeedObject::createFromPlace).collect(Collectors.toList()));
        Collections.shuffle(feedObjects);
        return null;
    }

}
