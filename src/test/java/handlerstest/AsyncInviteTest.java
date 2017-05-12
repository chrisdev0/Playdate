package handlerstest;

import apilayer.Constants;
import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.AttendanceInviteHandler;
import apilayer.handlers.asynchandlers.PlaydateHandler;
import dblayer.InviteDao;
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

import static apilayer.handlers.Paths.QueryParams.*;

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

        String res = (String) AttendanceInviteHandler.handleAttendPublicPlaydate(request, response);
        log.info("Result = " + res);

        Set<Playdate> playdateWhoUserIsAttending = PlaydateDAO.getInstance().getPlaydateWhoUserIsAttending(user);
        assertNotNull(playdateWhoUserIsAttending);

        playdateWhoUserIsAttending.forEach(playdate1 -> assertTrue(playdate.equals(playdate1)));

        remove(playdate);
        remove(user);
        remove(place);

    }


    /** Testar att
     * 1. Skicka en invite till en användare
     * 2. att användaren fått invite
     * 3. Acceptera invite
     * 4. att accepterad invite har försvunnit från listan med invites
     * 5. och att user är en participant i playdate som invite var för
     * */
    @Test
    public void testAcceptInvite() {
        User invited = createUser();
        User owner = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(owner, place);

        save(invited);
        save(owner);
        save(place);
        save(playdate);

        Request request = initRequestMock(owner);
        Response response = initResponseMock();

        injectKeyValue(request, new KeyValue(USER_BY_ID, invited.getId()),
                new KeyValue(INVITE_MSG, "invitemsg"),
                new KeyValue(PLAYDATE_BY_ID, playdate.getId()));

        String json = (String) AttendanceInviteHandler.handleSendInviteToPlaydate(request, response);
        assertNotNull(json);
        assertTrue(json.isEmpty());

        Request request1 = initRequestMock(invited);
        Response response1 = initResponseMock();

        String invitesOfLoggedInUser = (String) AttendanceInviteHandler.getInvitesOfLoggedInUser(request1, response1);
        assertNotNull(invitesOfLoggedInUser);
        log.info(invitesOfLoggedInUser);
        assertTrue(invitesOfLoggedInUser.length() > 2);

        Request request2 = initRequestMock(invited);
        Response response2 = initResponseMock();

        String inviteId = invitesOfLoggedInUser.substring(0, invitesOfLoggedInUser.indexOf(","));
        inviteId = inviteId.substring(inviteId.indexOf(":")+1, inviteId.length());
        log.info(inviteId);
        injectKeyValue(request2, new KeyValue(INVITE_BY_ID, inviteId));

        String res = (String) AttendanceInviteHandler.handleAcceptInviteToPlaydate(request2, response2);
        assertNotNull(res);
        assertTrue(res.isEmpty());

        String invitesOfLoggedInUserAfterAccept = (String) AttendanceInviteHandler.getInvitesOfLoggedInUser(request1, response1);
        assertNotNull(invitesOfLoggedInUserAfterAccept);
        assertTrue(invitesOfLoggedInUserAfterAccept.length() == 2);

        Request request3 = initRequestMock(invited);
        Response response3 = initResponseMock();

        injectKeyValue(request3, new KeyValue(Paths.QueryParams.PLAYDATE_BY_ID, playdate.getId()));

        String attendance = (String) PlaydateHandler.handleGetAttendanceForPlaydate(request3, response3);
        assertNotNull(attendance);
        assertTrue(attendance.length() > 2);

        remove(playdate);
        remove(place);
        remove(owner);
        remove(invited);
    }



    @Test
    public void noPlaydateWithId() {
        Request request = initRequestMock(createUser());
        Response response = initResponseMock();
        injectKeyValue(request, "playdateId", "-15");
        String res = (String) AttendanceInviteHandler.handleAttendPublicPlaydate(request, response);
        assertTrue(res.equals(Constants.MSG.NO_PLAYDATE_WITH_ID));
    }

    @Test(expected = HaltException.class)
    public void IllegalPlaydateId() {
        Request request = initRequestMock(createUser());
        Response response = initResponseMock();
        injectKeyValue(request, "playdateId", "abc");
        String res = (String) AttendanceInviteHandler.handleAttendPublicPlaydate(request, response);
    }

}
