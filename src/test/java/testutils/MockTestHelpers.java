package testutils;

import apilayer.Constants;
import model.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockTestHelpers extends TestStarter {


    public Request initRequestMock(User user) {
        Request request = mock(Request.class);
        Session session = mock(Session.class);
        when(request.session()).thenReturn(session);
        when(session.attribute(Constants.USER_SESSION_KEY)).thenReturn(user);
        when(request.session().attributes()).thenReturn(new HashSet<>());

        return request;
    }

    public Response initResponseMock() {
        return mock(Response.class);
    }


    public void injectKeyValue(Request request, KeyValue... keyValues) {
        for (KeyValue e : keyValues) {
            when(request.queryParams(e.key)).thenReturn(e.value);
        }
    }

    public boolean responseContainsId(String res, long id) {
        return res.contains("\"id\":" + id);
    }

    public void injectKeyValue(Request request, String key, String value) {
        when(request.queryParams(key)).thenReturn(value);
    }

    public void assertModelContains(ModelAndView modelAndView, Object object, boolean assertMode) {
        if (assertMode) {
            assertTrue(modelAndView.getModel().toString().contains(object.toString()));
        } else {
            assertFalse(modelAndView.getModel().toString().contains(object.toString()));
        }
    }


    public class KeyValue {
        String key, value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public KeyValue(String key, Long value) {
            this.key = key;
            this.value = "" + value;
        }
    }

}
