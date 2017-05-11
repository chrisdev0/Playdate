import apilayer.handlers.asynchandlers.SearchHandlers;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.HashSet;

import static org.mockito.Mockito.*;

public class MockingTest {


    @Test
    public void test() {
        Request request = mock(Request.class);
        Session session = mock(Session.class);

        when(request.session()).thenReturn(session);
        when(session.attribute("user")).thenReturn(new User("name", "email"));


        when(request.session().attributes()).thenReturn(new HashSet<>());
        when(request.queryParams("searchTerm")).thenReturn("norra");
        Response response = mock(Response.class);
        Object o = SearchHandlers.searchPlaces(request, response);
        System.out.println(o);
    }

}
