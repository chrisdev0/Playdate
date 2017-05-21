package statichandlers;

import apilayer.handlers.Paths;
import apilayer.handlers.templateHandlers.GetOneUserHandler;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.Friendship;
import model.FriendshipRequest;
import model.User;
import org.junit.Test;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;


import java.util.Optional;

import static testutils.ModelCreators.*;
import static org.junit.Assert.*;

@Slf4j
public class TestGetUser extends MockTestHelpers {


    @Test
    public void testGetUser() {
        User user = createUser();
        User otherUser = createUser();
        save(user);
        save(otherUser);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request, new KeyValue(Paths.QueryParams.USER_BY_ID, otherUser.getId()));

        ModelAndView modelAndView = new GetOneUserHandler().handleTemplateFileRequest(request, response);
        assertModelContains(modelAndView,user,true);
        assertModelContains(modelAndView,otherUser,true);

        remove(user);
        remove(otherUser);
    }

    @Test
    public void testGetUserIllegalId() {
        User user = createUser();
        User otherUser = createUser();
        save(user);
        save(otherUser);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request, new KeyValue(Paths.QueryParams.USER_BY_ID, -20L));

        ModelAndView modelAndView = null;
        try {
            modelAndView = new GetOneUserHandler().handleTemplateFileRequest(request, response);
            fail();
        } catch (Exception e) {

        }


        remove(user);
        remove(otherUser);
    }

    @Test
    public void testGetUserNoId() {
        User user = createUser();
        User otherUser = createUser();
        save(user);
        save(otherUser);

        Request request = initRequestMock(user);
        Response response = initResponseMock();


        try {
            ModelAndView modelAndView = new GetOneUserHandler().handleTemplateFileRequest(request, response);
            fail();
        } catch (Exception e) {

        }

        remove(user);
        remove(otherUser);
    }

    @Test
    public void testFriendSettingSameUser() {
        User user = createUser();
        save(user);

        testFriendSetting(user, user, 0);

        remove(user);
    }

    @Test
    public void testFriendSettingNotFriends() {
        User user = createUser();
        User otherUser = createUser();
        save(user);
        save(otherUser);

        testFriendSetting(user, otherUser, 4);

        remove(user);
        remove(otherUser);
    }

    @Test
    public void testFriendSettingFriends() {
        User user = createUser();
        User otherUser = createUser();
        save(user);
        save(otherUser);

        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(user, otherUser);
        assertTrue(friendshipRequest.isPresent());

        Optional<Friendship> friendship = UserDAO.getInstance().createFriendship(friendshipRequest.get());
        assertTrue(friendship.isPresent());


        testFriendSetting(user, otherUser, 1);

        remove(user);
        remove(otherUser);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testFriendSettingFriendRequest() {
        User user = createUser();
        User otherUser = createUser();
        save(user);
        save(otherUser);

        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(user, otherUser);
        assertTrue(friendshipRequest.isPresent());



        testFriendSetting(user, otherUser, 3);

        remove(user);
        remove(otherUser);
    }

    @Test
    public void testFriendSettingFriendRequestFromOtherUser() {
        User user = createUser();
        User otherUser = createUser();
        save(user);
        save(otherUser);

        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(otherUser,user);
        assertTrue(friendshipRequest.isPresent());



        testFriendSetting(user, otherUser, 2);

        remove(user);
        remove(otherUser);
    }



    public void testFriendSetting(User user, User otherUser, int expectedSetting) {
        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request, Paths.QueryParams.USER_BY_ID, "" + otherUser.getId());

        ModelAndView modelAndView = new GetOneUserHandler().handleTemplateFileRequest(request, response);
        assertModelContains(modelAndView,"friendsetting=" + expectedSetting,true);
    }




}
