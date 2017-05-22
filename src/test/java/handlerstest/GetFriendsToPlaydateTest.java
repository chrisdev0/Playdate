package handlerstest;

import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.FriendsHandler;
import apilayer.handlers.asynchandlers.FriendshipHandler;
import apilayer.handlers.asynchandlers.PlaydateHandler;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.FriendshipRequest;
import model.Place;
import model.Playdate;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;
import static org.junit.Assert.*;
import static testutils.ModelCreators.*;


@Slf4j
public class GetFriendsToPlaydateTest extends MockTestHelpers {


    @Test
    public void getPotentialFriends() {
        User user = createUser();
        User friend = createUser();
        User friendAttending = createUser();
        User notFriend = createUser();
        User haveFriendRequest = createUser();

        Place place = createPlace();
        Playdate playdate = createPlaydate(user, place);

        save(user);
        save(friend);
        save(friendAttending);
        save(notFriend);
        save(haveFriendRequest);
        save(place);
        save(playdate);

        FriendshipRequest friendship = save(user, friend);
        FriendshipRequest friendship2 = save(user, friendAttending);
        FriendshipRequest friendshipRequest2 = save(user, haveFriendRequest);
        save(friendship);
        save(friendship2);


        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request, new KeyValue(Paths.QueryParams.PLAYDATE_BY_ID, playdate.getId()));

        String res = (String) PlaydateHandler.handleGetFriendsToInvite(request, response);
        log.info(res);

        UserDAO.getInstance().removeFriendship(user, friend);
        boolean b = UserDAO.getInstance().removeOrDeclineFriendshipRequest(haveFriendRequest, user);
        log.info("removed fr request = " + b);
        remove(playdate);
        remove(place);
        remove(haveFriendRequest);
        remove(friend);
        remove(friendAttending);
        remove(user);
        remove(notFriend);
    }


}
