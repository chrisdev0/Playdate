package handlerstest;

import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.FriendsHandler;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import static testutils.ModelCreators.*;
import static org.junit.Assert.*;

@Slf4j
public class PotentialFriendsHandlerTest extends MockTestHelpers {

    @Test
    public void testCorrect() {

        User user = createUser();
        User friend = createUser();
        User notpotentialfriend = createUser();
        notpotentialfriend.setName("---------123123123213");
        friend.setName("abcdefg");
        assertEquals("abcdefg", friend.getName());
        save(user);
        save(notpotentialfriend);
        save(friend);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.OFFSET, "" + 0),
                new KeyValue(Paths.QueryParams.SEARCH_TERM, "abcdefg")
        );


        String res = (String) FriendsHandler.getPotentialFriends(request, response);
        log.info(res);


        remove(user);
        remove(friend);

    }


}
