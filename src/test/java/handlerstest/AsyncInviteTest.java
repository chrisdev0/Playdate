package handlerstest;

import apilayer.handlers.asynchandlers.AttendanceHandler;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import model.User;
import org.junit.Test;
import spark.HaltException;
import spark.Request;
import spark.Response;
import util.MockTestHelpers;

import java.util.Set;

import static org.junit.Assert.*;
import static util.ModelCreators.*;

@Slf4j
public class AsyncInviteTest extends MockTestHelpers{


    @Test
    public void testAttendPublicPlaydate() {
        Place place = createPlace();
        User user = createUser();
        Playdate playdate = createPlaydate(user, place);
        save(user);
        save(place);
        save(playdate);

        Request request = initRequestMock(user);
        Response response = initResponseMock();
        injectKeyValue(request, "playdateId", "" + playdate.getId());

        String res = (String) AttendanceHandler.handleAttendPublicPlaydate(request, response);
        log.info("Result = " + res);

        Set<Playdate> playdateWhoUserIsAttending = PlaydateDAO.getInstance().getPlaydateWhoUserIsAttending(user);
        assertNotNull(playdateWhoUserIsAttending);

        playdateWhoUserIsAttending.forEach(playdate1 -> assertTrue(playdate.equals(playdate1)));

        remove(playdate);
        remove(user);
        remove(place);

    }





    @Test
    public void noPlaydateWithId() {
        Request request = initRequestMock(createUser());
        Response response = initResponseMock();
        injectKeyValue(request, "playdateId", "-15");
        String res = (String) AttendanceHandler.handleAttendPublicPlaydate(request, response);
        assertTrue(res.equals("no_playdate"));
    }

    @Test(expected = HaltException.class)
    public void IllegalPlaydateId() {
        Request request = initRequestMock(createUser());
        Response response = initResponseMock();
        injectKeyValue(request, "playdateId", "abc");
        String res = (String) AttendanceHandler.handleAttendPublicPlaydate(request, response);
    }

}
