package apilayer.handlers.templateHandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.asynchandlers.SparkHelper;
import dblayer.PlaydateDAO;
import model.Playdate;
import model.User;
import spark.Request;

import java.util.*;
import java.util.stream.Collectors;

public class GetMyEventsHandler extends StaticFileTemplateHandlerImpl {

    public GetMyEventsHandler() {
        super("my-events.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        User user = SparkHelper.getUserFromSession(request);

        Set<Playdate> allPlaydateWhoUserIsAttendingAlsoOwner = PlaydateDAO.getInstance().getAllPlaydateWhoUserIsAttendingAlsoOwner(user);

        map.put("myplaydates", allPlaydateWhoUserIsAttendingAlsoOwner
                .stream()
                .filter(Playdate::playdateIsInFuture)
                .sorted().collect(Collectors.toList()));

        map.put("playdatesthroughfriend", PlaydateDAO.getInstance().getPlaydatesWhoUserIsNotAttendingButCanAttendThroughFriend(user)
                .stream()
                .filter(Playdate::playdateIsInFuture)
                .sorted().collect(Collectors.toList()));

        int[] grid = SparkHelper.getGridLocationFromRequest(request);
        Optional<List<Playdate>> publicPlaydatesByLoc = PlaydateDAO.getInstance().getPublicPlaydatesByLoc(grid[0], grid[1]);
        publicPlaydatesByLoc.ifPresent(playdates -> map.put("closepublicplaydates", playdates
                .stream()
                .filter(Playdate::playdateIsInFuture)
                .sorted().collect(Collectors.toList())));

        return Optional.of(map);
    }
}
