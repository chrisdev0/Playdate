package handlerstest;

import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.FriendsHandler;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.Friendship;
import model.FriendshipRequest;
import model.User;
import org.junit.After;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;
import testutils.ModelCreators;

import java.util.ArrayList;
import java.util.Optional;

import static testutils.ModelCreators.*;
import static org.junit.Assert.*;

@Slf4j
public class PotentialFriendsHandlerTest extends MockTestHelpers {

    private final ArrayList<User> users = new ArrayList<>();

    @Test
    public void testCorrect() {

        User user = createUser();
        User friend = createUser();
        User notpotentialfriend = createUser();
        User potentialFriend = createUser();
        users.add(user);
        users.add(friend);
        users.add(notpotentialfriend);
        users.add(potentialFriend);
        notpotentialfriend.setName("---------123123123213");
        notpotentialfriend.setDescription("ska inte vara med");
        friend.setName("abcdefg");
        friend.setDescription("ska inte vara med");
        potentialFriend.setName("abcdefg");
        potentialFriend.setDescription("Ska vara med");




        assertEquals("abcdefg", friend.getName());
        save(user);
        save(notpotentialfriend);
        save(potentialFriend);
        save(friend);
        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(friend, user);
        assertTrue(friendshipRequest.isPresent());

        Optional<Friendship> friendship = UserDAO.getInstance().createFriendship(friendshipRequest.get());
        assertTrue(friendship.isPresent());
        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.SEARCH_TERM, "abcdefg")
        );


        String res = (String) FriendsHandler.getPotentialFriends(request, response);
        log.info(res);
        assertFalse(responseContainsId(res,friend.getId()));
        assertTrue(responseContainsId(res,potentialFriend.getId()));
        assertFalse(responseContainsId(res,notpotentialfriend.getId()));
        assertFalse(responseContainsId(res,user.getId()));

        UserDAO.getInstance().removeFriendship(friend, user);
    }

    @After
    public void removeData() {

        users.forEach(ModelCreators::remove);

    }


}
