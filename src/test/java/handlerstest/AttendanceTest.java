package handlerstest;

import apilayer.Constants;
import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.AttendanceInviteHandler;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import static org.junit.Assert.*;
import static testutils.ModelCreators.*;

@Slf4j
public class AttendanceTest extends MockTestHelpers {

    @Test
    public void testRemoveAttendance() {
        User user = createUser();
        User attendant = createUser();
        save(user);
        save(attendant);
        Place place = createPlace();
        save(place);
        Playdate playdate = createPlaydate(user, place);
        save(playdate);

        PlaydateDAO.getInstance().addAttendance(attendant, playdate);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.USER_BY_ID,attendant.getId()),
                new KeyValue(Paths.QueryParams.PLAYDATE_BY_ID,playdate.getId())
        );

        String res = (String) AttendanceInviteHandler.handleKickUserFromPlaydate(request, response);
        log.info(res);
        assertEquals(Constants.MSG.OK, res);

        remove(place);
        remove(playdate);
        remove(user);
        remove(attendant);
    }

}
