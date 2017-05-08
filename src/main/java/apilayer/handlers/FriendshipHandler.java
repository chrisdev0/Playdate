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

        if (friend.isPresent()) {
            if (UserDAO.getInstance().createFriendshipRequest(user, friend.get())) {
                return "";
            }
        }
        throw halt(400);
    }



    public void removeFriend(){
        /*
        Ta bort vän från Friends-listan
         */
    }

    public void addFriend(){
        /*
        Överföra från friendRequest-listan till Friends-listan för båda användare.
        (och ta bort den från friendRequest-listan)
         */
    }




}
