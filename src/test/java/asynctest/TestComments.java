package asynctest;

import apilayer.handlers.CommentHandler;
import apilayer.handlers.asynchandlers.CommentsHandler;
import lombok.extern.slf4j.Slf4j;
import model.Comment;
import model.Place;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import util.MockTestHelpers;

import static util.ModelCreators.*;
import static org.junit.Assert.*;

@Slf4j
public class TestComments extends MockTestHelpers {

    @Test
    public void testMakeComment() {
        User user = createUser();
        Place place = createPlace();

        save(user);
        save(place);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request, new KeyValue("placeId", "" + place.getId()), new KeyValue("comment", "kommentar"));

        String json = (String)CommentsHandler.handlePostComment(request, response);
        log.info("statuscode = " + response.status());

        log.info(json);


        remove(user);
        remove(place);
    }


}
