import apilayer.Constants;
import apilayer.handlers.FriendshipHandler;
import apilayer.handlers.PlaceHandler;
import apilayer.handlers.asynchandlers.SearchHandlers;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import spark.Session;
import util.ModelCreators;

import java.util.HashSet;

import static org.mockito.Mockito.*;
import static util.ModelCreators.remove;
import static util.ModelCreators.save;

public class MockingTest {


    private Request initRequestMock(User user) {
        Request request = mock(Request.class);
        Session session = mock(Session.class);
        when(request.session()).thenReturn(session);
        when(session.attribute(Constants.USER_SESSION_KEY)).thenReturn(user);
        when(request.session().attributes()).thenReturn(new HashSet<>());

        return request;
    }

    private Response initResponseMock() {
        return mock(Response.class);
    }

    @Test
    public void test() {
        User user = ModelCreators.createUser();
        Request request = initRequestMock(user);
        Response response = initResponseMock();

        when(request.queryParams("name")).thenReturn("norr");
        when(request.queryParams("offset")).thenReturn("0");
        Object o = PlaceHandler.handleGetPlaceByGeoArea(request, response);
        System.out.println(o);
    }


    @Test
    public void testHandler() {
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        Session session = mock(Session.class);
        when(request.session()).thenReturn(session);
        User user = ModelCreators.createUser();
        save(user);
        User friend = ModelCreators.createUser();
        save(friend);

        when(request.session().attribute(Constants.USER_SESSION_KEY)).thenReturn(user);
        when(request.queryParams("id")).thenReturn("" + friend.getId());


        FriendshipHandler.addFriendRequest(request, response);

        remove(friend);
        remove(user);
    }

}
