package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import model.User;
import spark.Request;
import spark.Response;
import utils.fakedata.Data;

import java.util.Optional;

import static apilayer.handlers.asynchandlers.SparkHelper.*;

@Slf4j
public class DataFillerHandler {


    public static Object injectData(Request request, Response response) {
        User user = getUserFromSession(request);
        Data.createFriend(user);
        Data.createFriend(user);
        Data.createFriend(user);
        Data.createFriend(user);
        Data.userCreatedPlaydateInNearFutureRandomPlace(user);
        Data.userCreatedPlaydateInNearFutureRandomPlace(user);
        Data.userCreatedPlaydateInNearFutureRandomPlace(user);
        Data.userCreatedPlaydateInNearFutureRandomPlace(user);
        Data.createUserWhoUserSendFriendrequestTo(user);
        Data.createUserWhoUserSendFriendrequestTo(user);
        Data.createUserWhoUserSendFriendrequestTo(user);
        Data.createUserWhoHasSentFriendRequestToUser(user);
        Data.createUserWhoHasSentFriendRequestToUser(user);
        Data.createUserWhoHasSentFriendRequestToUser(user);
        Data.createUserWhoHasSentFriendRequestToUser(user);
        Data.createUserWhoHasSentFriendRequestToUser(user);
        return setStatusCodeAndReturnString(response, 200, Constants.MSG.OK);
    }

    public static Object forceLoginAsUser(Request request, Response response) {
        Optional<User> userFromRequestById = getUserFromRequestById(request);
        if (!userFromRequestById.isPresent()) {
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.ERROR);
        } else {
            log.info("IP" + request.ip() + "Forced login as user " + userFromRequestById.get().getId());
            request.session(true).attribute(Constants.USER_SESSION_KEY, userFromRequestById.get());
            response.redirect("/protected/feed");
            return "";
        }
    }


}
