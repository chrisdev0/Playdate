package apilayer.handlers;

import apilayer.Constants;
import dblayer.HibernateUtil;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;

import javax.swing.text.html.parser.Parser;
import java.util.Optional;
import java.util.Set;

import static spark.Spark.halt;



@Slf4j
public class FriendshipHandler {


    /*
    Skicka vänskapsförfrågan och lägg till i friendRequest-listan
    */

    public static Object addFriendRequest(Request request, Response response) {
        String friendIdParam = request.queryParams("id");
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

        Optional<User> friend = UserDAO.getUserById(friendId);

        if (friend.isPresent()){
            if (UserDAO.getInstance().checkIfFriendWithUser(user.getId(), friend.get().getId()).isPresent()){
                log.info("Users are already friends");
                return "";
            }

            if (UserDAO.getInstance().checkIfFriendRequestSent(user.getId(), friend.get().getId()).isPresent()){
                log.info("A friend request has already been sent");
                return "";
            }

        }


        if (friend.isPresent()) {
            if (UserDAO.getInstance().createFriendshipRequest(user, friend.get())) {
                log.info("Friend request has been sent");
                return "";
            }
        }

        throw halt(400);
    }


    public void removeFriend(Request request, Response response){

        String friendIdParam = request.queryParams("friendId");
        Long friendId = ParserHelpers.parseToLong(friendIdParam);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        Optional<User> friend = UserDAO.getUserById(friendId);

        if (user == null || user.getId() == friendId) {
            log.error("User is null");
            throw halt(400, "user is null");
        }

        if (friend.isPresent()) {
            //gör en koll att friendshipen innehåller dessa vänner.
            if (UserDAO.getInstance().deleteFriendship(user, friend.get())); {
                log.info("Friend has been deleted");
                //return ""; ?????
            }
        }
    }

    public void declineReceivedFriendshipRequest(Request request, Response response){
        /*
        Göra alla tester för att se till att användaren är receiver,
        Sedan kalla på declineFriendRequest i UserDAO
        */

        String friendIdParam = request.queryParams("friendId");
        Long friendId = ParserHelpers.parseToLong(friendIdParam);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        if (user == null || user.getId() != friendId){
            log.error("User is null");
            throw halt(400, "user is null");
        }

        Optional<User> friend = UserDAO.getUserById(friendId);



    }

    public void acceptFriendRequest(Request request, Response response){
        /*
        Överföra från friendRequest-listan till Friends-listan för båda användare.
        (och ta bort den från friendRequest-listan)
         */
        String friendIdParam = request.queryParams("friendId");
        Long friendId = ParserHelpers.parseToLong(friendIdParam);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        if (user == null || user.getId() == friendId){
            log.error("User is null");
            throw halt(400, "user is null");
        }

        Optional<User> friend = UserDAO.getUserById(friendId);
        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().checkIfFriendRequestSent(user.getId(), friend.get().getId());

        if(!friendshipRequest.isPresent()) {
            log.error("Friendship request has not been sent");
            throw halt(400, "No valid friendship request found");
        }

        if(friend.isPresent()) {
            if (UserDAO.getInstance().createFriendship(user, friend.get())) {
                log.info("Friendship has been approved");

            }
        }

    }

}
