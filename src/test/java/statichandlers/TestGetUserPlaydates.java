package statichandlers;

import apilayer.handlers.Paths;
import apilayer.handlers.templateHandlers.GetOnePlaceHandler;
import apilayer.handlers.templateHandlers.GetUserPlaydateHandler;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import model.User;
import org.junit.Test;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import static org.junit.Assert.*;
import static testutils.ModelCreators.*;


@Slf4j
public class TestGetUserPlaydates extends MockTestHelpers{


    @Test
    public void testGetUserPlaydates() {
        User user = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(user, place);
        Playdate playdate2 = createPlaydate(user, place);
        save(user);
        save(place);
        save(playdate);
        save(playdate2);

        Request request = initRequestMock(user);
        Response response = initResponseMock();


        ModelAndView modelAndView = new GetUserPlaydateHandler().handleTemplateFileRequest(request, response);
        log.info(modelAndView.getModel().toString());
        assertModelContains(modelAndView,playdate,true);
        assertModelContains(modelAndView,playdate2,true);


        remove(place);
        remove(playdate);
        remove(user);
    }




}
