package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import com.google.gson.GsonBuilder;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.Playdate;
import model.User;
import spark.Request;
import spark.Response;
import utils.filters.TimeFilterable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static apilayer.handlers.asynchandlers.SparkHelper.getUserFromSession;

@Slf4j
public class EventHandler {


    public static Object getAllPlaydatesWhoUserIsAttendingOrOwnerFuture(Request request, Response response) {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                .toJson(PlaydateDAO.getInstance()
                        .getAllPlaydateWhoUserIsAttendingAlsoOwner(getUserFromSession(request),
                                TimeFilterable.TimeFilter.FUTURE));
    }

    public static Object getAllPlaydatesWhoUserIsNotAttendingButCanAttendThroughFriendFuture(Request request, Response response) {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                .toJson(PlaydateDAO.getInstance()
                        .getPlaydatesWhoUserIsNotAttendingButCanAttendThroughFriend(getUserFromSession(request),
                                TimeFilterable.TimeFilter.FUTURE));
    }

    public static Object getPublicPlaydatesCloseToUserFuture(Request request, Response response){
        int[] grid = SparkHelper.getGridLocationFromRequest(request);
        log.info("using following grid=" + Arrays.toString(grid));
        Optional<List<Playdate>> publicPlaydatesByLoc = PlaydateDAO.getInstance().getPublicPlaydatesByLoc(grid[0], grid[1], TimeFilterable.TimeFilter.FUTURE);
        if (publicPlaydatesByLoc.isPresent()) {
            log.info("returned " + publicPlaydatesByLoc.get().size() + " playdates");
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                    .toJson(publicPlaydatesByLoc.get());
        } else {
            return SparkHelper.setStatusCodeAndReturnString(response, 400, Constants.MSG.ERROR);
        }
    }

}
