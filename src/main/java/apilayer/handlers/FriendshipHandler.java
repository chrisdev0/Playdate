package apilayer.handlers;

import apilayer.Constants;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;

import java.util.Optional;

import static spark.Spark.halt;


@Slf4j
public class FriendshipHandler {


    /*
    Skicka vänskapsförfrågan och lägg till i friendRequest-listan
    */

    public static Object addFriendRequest(Request request, Response response) {
        String friendIdParam = request.queryParams("id");
        String reportDescription = request.queryParams("reportDesc");
        Long friendId = ParserHelpers.parseToLong(friendIdParam);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        if (user == null || user.getId().equals(friendId)) {
            log.error("User is null or same user id sent");
            throw halt(400, "user is null or same friend id as current user sent");
        }

        /*
        Kolla om det finns en vänförfrågan sedan tidigare
         */

        /*
        Göra kontroll om person A är vän med person B eller vice versa
         */

        Optional<User> friend = UserDAO.getInstance().getUserById(friendId);

        if (friend.isPresent()) {
            if (UserDAO.getInstance().checkIfFriendWithUser(user.getId(), friend.get().getId()).isPresent()) {
                log.info("Users are already friends");
                throw halt(400, "Users are already friends");
            }
        }


        if (friend.isPresent()) {
            if (UserDAO.getInstance().createFriendshipRequest(user, friend.get()).isPresent()) {
                log.info("Friend request has been sent");
                response.status(200);
                return "";
            }
        }

        throw halt(400);
    }


    public static Object removeFriend(Request request, Response response) {

        String friendIdParam = request.queryParams("friendId");
        Long friendId = ParserHelpers.parseToLong(friendIdParam);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        Optional<User> friend = UserDAO.getInstance().getUserById(friendId);

        if (!friend.isPresent()) {
            log.info("No user with that Id exists");
            throw halt(400);
        }

        Optional<Friendship> friendship = UserDAO.getInstance().checkIfFriendWithUser(user.getId(), friend.get().getId());

        if (!friendship.isPresent()) {
            log.info("Users are not friends");
            throw halt(400);
        }

        if (user.getId().equals(friendId)) {
            log.error("User is null");
            throw halt(400, "user is null");
        }

        //gör en koll att friendshipen innehåller dessa vänner.
        if (UserDAO.getInstance().deleteFriendship(friendship.get())) {
            log.info("Friend has been deleted");
            response.status(200);
            return "";
        }
        throw halt(400);
    }

    public static Object declineReceivedFriendshipRequest(Request request, Response response) {
        /*
        Göra alla tester för att se till att användaren är receiver,
        Sedan kalla på declineFriendRequest i UserDAO
        */

        String friendIdParam = request.queryParams("friendId");
        Long friendId = ParserHelpers.parseToLong(friendIdParam);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        if (user == null || user.getId().equals(friendId)) {
            log.error("User is null");
            throw halt(400, "user is null");
        }

        Optional<User> friend = UserDAO.getInstance().getUserById(friendId);
        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().checkIfFriendRequestSent(user.getId(), friendId);

        if (!friendshipRequest.isPresent()) {
            log.error("No friend request has been sent");
            throw halt(400, "No valid friend request found");
        }

        if (friend.isPresent()) {
            if(UserDAO.getInstance().declineFriendRequest(user, friend.get())) {
                response.status(200);
                return "";
            }
        }
        throw halt(400);
    }

    public static Object acceptFriendRequest(Request request, Response response) {
        /*
        Överföra från friendRequest-listan till Friends-listan för båda användare.
        (och ta bort den från friendRequest-listan)
         */
        String friendIdParam = request.queryParams("friendId");
        Long friendId = ParserHelpers.parseToLong(friendIdParam);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        if (user == null || user.getId().equals(friendId)) {
            log.error("User is null");
            throw halt(400, "user is null");
        }

        Optional<User> friend = UserDAO.getInstance().getUserById(friendId);
        if (!friend.isPresent()) {
            log.info("Friend user does not exist");
            throw halt(400);
        }
        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().checkIfFriendRequestSent(user.getId(), friend.get().getId());

        if (!friendshipRequest.isPresent()) {
            log.error("Friendship request has not been sent");
            throw halt(400, "No valid friendship request found");
        }
        if (UserDAO.getInstance().createFriendship(friendshipRequest.get()).isPresent()) {
            log.info("Friendship has been approved");
            response.status(200);
            return "";

        }
        throw halt(400);
    }

    /*
    Hämta mina vänskapsförfrågningar, hämta mina kompisar,
     */

}
