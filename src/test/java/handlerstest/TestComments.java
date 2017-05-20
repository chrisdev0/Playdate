package handlerstest;

import apilayer.Constants;
import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.CommentsHandler;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import static apilayer.handlers.Paths.QueryParams.*;
import static testutils.ModelCreators.*;
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

        String json = (String)CommentsHandler.handlePostPlaceComment(request, response);
        log.info("statuscode = " + response.status());

        log.info(json);


        remove(user);
        remove(place);
    }


    @Test
    public void testPostAndGetComment() {
        User user = createUser();
        Place place = createPlace();

        save(user);
        save(place);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request, new KeyValue(Paths.QueryParams.PLACE_BY_ID, place.getId()), new KeyValue(Paths.QueryParams.COMMENT_CONTENT, "ganska lång kommentar kommentar"));

        String json = (String) CommentsHandler.handlePostPlaceComment(request, response);

        Request request1 = initRequestMock(user);
        Response response1 = initResponseMock();

        injectKeyValue(request1, new KeyValue(Paths.QueryParams.PLACE_BY_ID, place.getId()));

        String json1 = (String) CommentsHandler.handleGetCommentsOfPlace(request1, response);

        assertEquals(json, json1);

        remove(user);
        remove(place);
    }

    @Test
    public void testPostIllegalCommentNoPlace() {
        Request request = initRequestMock(createUser());
        Response response = initResponseMock();

        injectKeyValue(request, new KeyValue(Paths.QueryParams.PLACE_BY_ID, -2000L));
        String answer = (String) CommentsHandler.handleGetCommentsOfPlace(request, response);
        assertNotNull(answer);
        assertEquals(answer, Constants.MSG.NO_PLACE_WITH_ID);
    }



}
