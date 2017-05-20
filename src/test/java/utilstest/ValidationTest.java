package utilstest;
import apilayer.Constants;
import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.PlaydateHandler;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import model.PlaydateVisibilityType;
import model.User;
import org.junit.Test;
import spark.*;
import testutils.MockTestHelpers;
import testutils.ModelCreators;
import utils.Utils;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static testutils.ModelCreators.createPlaydate;
import static testutils.ModelCreators.*;

@Slf4j
public class ValidationTest extends MockTestHelpers{


    @Test
    public void testMultiNotNullOrEmptyMultiNull() {
        assertFalse(Utils.isNotNullAndNotEmpty(null, null));
    }


    @Test
    public void testCorrect() {
        assertTrue(Utils.isNotNullAndNotEmpty("test", "test"));
    }


    @Test
    public void testCorrectAndNull() {
        assertFalse(Utils.isNotNullAndNotEmpty("test", null));
    }

    @Test
    public void testNullArray() {
        String[] n = null;
        assertFalse(Utils.isNotNullAndNotEmpty(n));
    }

    @Test
    public void testValidatePhoneNumber(){
       /*
        Prova telefonnummer som inte matchar reg-ex.
         */

       User user = createUser();

    }


    @Test
    public void testValidateIllegalDate(){
        /*
        Testa om datum framåt och bakåt i tiden funkar. Två asserts.
         */

        User user = ModelCreators.createUser();
        Place place = ModelCreators.createPlace();
        save(user);
        save(place);
        Playdate playdate = createPlaydate(user, place);
        save(playdate);
        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request, new KeyValue(Paths.QueryParams.HEADER, "testheader"),
                new KeyValue(Paths.QueryParams.DESCRIPTION, "här kommer en ganska lång beskrivning"),
                new KeyValue(Paths.QueryParams.VISIBILITY_ID, "1"),
                new KeyValue(Paths.QueryParams.STARTTIME, "100000000"),
                new KeyValue(Paths.QueryParams.PLACE_BY_ID, place.getId()));

        log.info("Systemtiden: " + System.currentTimeMillis());

        String s = (String) PlaydateHandler.handleMakePlaydate(request, response);
        log.info("Här är s: " + s);
        assertEquals(s, Constants.MSG.VALIDATION_ERROR + Constants.MSG.ValidationErrors.ERROR_STARTTIME);

        remove(playdate);
        remove(user);
        remove(place);

    }
}
