package dbtest;

import dblayer.InviteDao;
import lombok.extern.slf4j.Slf4j;
import model.Invite;
import model.Place;
import model.Playdate;
import model.User;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static util.ModelCreators.*;

@Slf4j
public class InviteDaoTest {


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

        Invite invite = new Invite("message", playdate, invited);
        save(invite, invited, playdate);
        assertNotNull(invite.getId());

        Optional<List<Invite>> invitesOfUser = InviteDao.getInstance().getInvitesOfUser(invited);
        assertTrue(invitesOfUser.isPresent());
        assertTrue(invitesOfUser.get().get(0).getId().equals(invite.getId()));

        remove(playdate);
        remove(place);
        remove(invited);
        remove(owner);
    }

}
