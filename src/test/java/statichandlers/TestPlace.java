package statichandlers;

import apilayer.handlers.Paths;
import apilayer.handlers.templateHandlers.GetOnePlaceHandler;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.User;
import org.junit.Test;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import static org.junit.Assert.*;
import static testutils.ModelCreators.*;


@Slf4j
public class TestPlace extends MockTestHelpers{


    @Test
    public void testGetPlace() {
        User user = createUser();
        Place place = createPlace();
        save(user);
        save(place);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.PLACE_BY_ID, place.getId()));

        ModelAndView modelAndView = new GetOnePlaceHandler().handleTemplateFileRequest(request, response);
        assertEquals("showplace.vm", modelAndView.getViewName());
        assertTrue(modelAndView.getModel().toString().contains(place.toString()));


        remove(place);
        remove(user);
    }


    @Test
    public void testGetIllegalPlace() {
        User user = createUser();
        Place place = createPlace();
        save(user);
        save(place);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.PLACE_BY_ID, -20L));

        ModelAndView modelAndView = new GetOnePlaceHandler().handleTemplateFileRequest(request, response);

        assertFalse(modelAndView.getModel().toString().contains("place=Place"));

        remove(place);
        remove(user);
    }


    @Test
    public void testGetPlaceNoId() {
        User user = createUser();
        Place place = createPlace();
        save(user);
        save(place);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        try {
            ModelAndView modelAndView = new GetOnePlaceHandler().handleTemplateFileRequest(request, response);
            fail();
        } catch (Exception e) {

        }


        remove(place);
        remove(user);
    }



}
