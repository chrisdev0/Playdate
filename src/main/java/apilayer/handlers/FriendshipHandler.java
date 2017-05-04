package apilayer.handlers;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import model.*;
import spark.Request;

import spark.Response;
import utils.ParserHelpers;

import static spark.Spark.halt;

/**
 * Created by martinsenden and frida on 2017-05-04.
 */

@Slf4j
public class FriendshipHandler {


    public static Object addFriend(Request request, Response response){
        String friendIdParam = request.queryParams("id");
        Long friendId = ParserHelpers.parseToLong(friendIdParam);

        User user = request.session().attribute(Constants.USER_SESSION_KEY);

        if (user == null || user.getId() != friendId){
            log.error("User is null or same user id sent");
            throw halt(400, "user is null or same friend id as current user sent");
        }



    /*
    Skicka vänskapsförfrågan och lägg till i friendRequest-listan
     */

    }

    public void removeFriend(){
        /*
        Ta bort vän från Friends-listan
         */
    }

    public void acceptFriendRequest(){
        /*
        Överföra från friendRequest-listan till Friends-listan för båda användare.
        (och ta bort den från friendRequest-listan)
         */
    }




}
