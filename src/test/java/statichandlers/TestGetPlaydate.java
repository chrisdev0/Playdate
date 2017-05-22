package statichandlers;

import apilayer.handlers.Paths;
import apilayer.handlers.templateHandlers.GetOnePlaydateHandler;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import model.User;
import org.junit.Test;
import spark.HaltException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import static org.junit.Assert.fail;
import static testutils.ModelCreators.*;

@Slf4j
public class TestGetPlaydate extends MockTestHelpers {


    @Test
    public void testGetPlaydate() {
        User user = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(user, place);
        save(user);
        save(place);
        save(playdate);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.PLAYDATE_BY_ID, playdate.getId())
        );

        ModelAndView modelAndView = new GetOnePlaydateHandler().handleTemplateFileRequest(request, response);
        assertModelContains(modelAndView,place,true);
        assertModelContains(modelAndView,playdate,true);


        remove(playdate);
        remove(place);
        remove(user);
    }


    @Test
    public void testGetPlaydateIllegalPlaydateId() {
        User user = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(user, place);
        save(user);
        save(place);
        save(playdate);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.PLAYDATE_BY_ID, -20L)
        );

        ModelAndView modelAndView = null;
        try {
            modelAndView = new GetOnePlaydateHandler().handleTemplateFileRequest(request, response);
            fail();
        } catch (Exception e) {

        }


        remove(playdate);
        remove(place);
        remove(user);
    }



    @Test
    public void testGetPlaydateNoId() {
        User user = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(user, place);
        save(user);
        save(place);
        save(playdate);

        Request request = initRequestMock(user);
        Response response = initResponseMock();


        try {
            ModelAndView modelAndView = new GetOnePlaydateHandler().handleTemplateFileRequest(request, response);
            fail();
        } catch (Exception e) {

        }


        remove(playdate);
        remove(place);
        remove(user);
    }



}
