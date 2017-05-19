package dbtest;

import dblayer.InviteDAO;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.Invite;
import model.Place;
import model.Playdate;
import model.User;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static testutils.ModelCreators.*;

@Slf4j
public class InviteDAOTest {


    @Test
    public void testCreateInvite() {
        User owner = createUser();
        User invited = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(owner, place);

        save(owner);
        save(invited);
        save(place);
        save(playdate);

        Invite invite = new Invite(playdate, invited);
        save(invite, invited, playdate);
        assertNotNull(invite.getId());

        Optional<List<Invite>> invitesOfUser = InviteDAO.getInstance().getInvitesOfUser(invited);
        assertTrue(invitesOfUser.isPresent());
        assertTrue(invitesOfUser.get().get(0).getId().equals(invite.getId()));

        remove(playdate);
        remove(place);
        remove(invited);
        remove(owner);
    }
    @Test
    public void testRemoveInvite(){
        User owner = createUser();
        User invited = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(owner, place);

        save(owner);
        save(invited);
        save(place);
        save(playdate);

        Invite invite = new Invite(playdate, invited);
        save(invite, invited, playdate);
        assertNotNull(invite.getId());

        Optional<List<Invite>> invitesOfUser = InviteDAO.getInstance().getInvitesOfUser(invited);
        assertTrue(invitesOfUser.isPresent());
        assertTrue(invitesOfUser.get().get(0).getId().equals(invite.getId()));

        log.info("Listan: " + invitesOfUser.toString());
        assertTrue(InviteDAO.getInstance().removeInvite(invite, playdate, invited));

        invitesOfUser = InviteDAO.getInstance().getInvitesOfUser(invited);
        log.info("Listan removed: " + invitesOfUser.toString());

        remove(playdate);
        remove(place);
        remove(invited);
        remove(owner);
    }

    @Test
    public void testAcceptInvite(){
        User owner = createUser();
        User invited = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(owner, place);

        save(owner);
        save(invited);
        save(place);
        save(playdate);

        Invite invite = new Invite(playdate, invited);
        save(invite, invited, playdate);
        assertNotNull(invite.getId());

        Optional<List<Invite>> invitesOfUser = InviteDAO.getInstance().getInvitesOfUser(invited);
        assertTrue(invitesOfUser.isPresent());
        assertTrue(invitesOfUser.get().get(0).getId().equals(invite.getId()));

        log.info("playdate: " + playdate.getId());

        invitesOfUser.get().forEach(invite1 -> assertEquals(invite1.getPlaydate().getId(), playdate.getId()));
        assertTrue(PlaydateDAO.getInstance().acceptAndAddAttendance(invite));

        log.info("added: " + invited.getAttendingPlaydates().toString());

        remove(playdate);
        remove(place);
        remove(invited);
        remove(owner);

    }
}
