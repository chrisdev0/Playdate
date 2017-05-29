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
import java.util.Set;
import java.util.stream.Collectors;

import static apilayer.handlers.asynchandlers.SparkHelper.*;

@Slf4j
public class FriendsHandler {

    public static Object handleAcceptFriendRequest(Request request, Response response) {
        Optional<User> userById = getUserFromRequestById(request);
        if (userById.isPresent()) {
            Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().checkIfFriendRequestSent(getUserFromSession(request).getId(), userById.get().getId());
            if (friendshipRequest.isPresent()) {
                if (UserDAO.getInstance().createFriendship(friendshipRequest.get()).isPresent()) {
                    return setStatusCodeAndReturnString(response, 200, Constants.MSG.OK);
                }
            }
        }
        return setStatusCodeAndReturnString(response, 200, Constants.MSG.ERROR);
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
        log.info("search term = " + search);
        List<User> collect = UserDAO.getInstance().getPotentialFriends(search, user)
                .stream().sorted((new SearchResultSorter(search)::compare)).collect(Collectors.toList());
        log.info("found " + collect.size() + " potential friends for term = " + search);
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create().toJson(collect);
    }

    public static Object getSentFriendRequest(Request request, Response response) {
        User user = getUserFromSession(request);
        Optional<Set<User>> sentFriendRequest = UserDAO.getInstance().getSentFriendRequest(user);
        if (sentFriendRequest.isPresent()) {
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                    .toJson(sentFriendRequest.get());
        } else {
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.ERROR);
        }
    }


}
