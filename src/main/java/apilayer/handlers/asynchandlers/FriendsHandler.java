package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import com.google.gson.GsonBuilder;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.Friendship;
import model.FriendshipRequest;
import model.User;
import spark.Response;
import spark.Request;
import utils.ParserHelpers;
import utils.sorters.SearchResultSorter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static apilayer.handlers.asynchandlers.SparkHelper.getOffsetFromRequest;
import static apilayer.handlers.asynchandlers.SparkHelper.getUserFromSession;

@Slf4j
public class FriendsHandler {

    public static Object handleAcceptFriendRequest(Request request, Response response) {
        String userIdStr = request.queryParams(Paths.QueryParams.USER_BY_ID);
        Long userId = ParserHelpers.parseToLong(userIdStr);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        Optional<User> userById = UserDAO.getInstance().getUserById(userId);
        if (userById.isPresent()) {
            Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().checkIfFriendRequestSent(user.getId(), userById.get().getId());
            if (friendshipRequest.isPresent()) {
                FriendshipRequest fr = friendshipRequest.get();
                Optional<Friendship> friendship = UserDAO.getInstance().createFriendship(fr);
                if (friendship.isPresent()) {
                    response.status(200);
                    return "";
                }
            }
        }
        response.status(400);
        return "";
    }

    public static Object handleDeclineFriendshipRequest(Request request, Response response) {
        String userIdStr = request.queryParams(Paths.QueryParams.USER_BY_ID);
        Long userId = ParserHelpers.parseToLong(userIdStr);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        Optional<User> userById = UserDAO.getInstance().getUserById(userId);
        if (userById.isPresent()) {
            log.info("found user with userid = " + userId);
            if (UserDAO.getInstance().declineFriendRequest(user, userById.get())) {
                log.info("deleted friendshiprequest");
                response.status(200);
                return "";
            }
            log.info("couldn't decline friendship request");
        }
        response.status(400);
        return "";
    }

    public static Object getPotentialFriends(Request request, Response response) {
        User user = getUserFromSession(request);
        String search = request.queryParams(Paths.QueryParams.SEARCH_TERM);
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create().toJson(UserDAO.getInstance().getPotentialFriends(search, user)
                        .stream().sorted((new SearchResultSorter(search)::compare)).collect(Collectors.toList()));
    }


}
