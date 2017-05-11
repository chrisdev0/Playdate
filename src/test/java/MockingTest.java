import apilayer.Constants;
import apilayer.handlers.FriendshipHandler;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import spark.Session;
import util.MockTestHelpers;
import util.ModelCreators;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static util.ModelCreators.remove;
import static util.ModelCreators.save;

public class MockingTest extends MockTestHelpers{


    @Test
    public void testMockingUtils() {
        Request request = initRequestMock(ModelCreators.createUser());
        Response response = initResponseMock();
        injectKeyValue(request, new KeyValue("a", "b"));
        assertEquals("b", request.queryParams("a"));
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
