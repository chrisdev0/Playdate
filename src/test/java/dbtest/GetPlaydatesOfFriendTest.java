package dbtest;

import dblayer.HibernateUtil;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.eclipse.jetty.util.log.Log;
import org.hibernate.Session;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static testutils.ModelCreators.*;

@Slf4j
public class GetPlaydatesOfFriendTest {


    @Test
    public void testGetPlaydatesOfFriends() {

        User user = createUser();
        User friend = createUser();
        User notFriend = createUser();

        Place place = createPlace();
        Playdate playdate = createPlaydate(friend, place);
        Playdate playdate2 = createPlaydate(notFriend, place);
        Playdate playdate3 = createPlaydate(friend, place);
        Playdate playdateWhoUserIsAttending = createPlaydate(friend, place);
        playdate.setPlaydateVisibilityType(PlaydateVisibilityType.FRIENDS_ONLY);
        playdate2.setPlaydateVisibilityType(PlaydateVisibilityType.FRIENDS_ONLY);
        playdate3.setPlaydateVisibilityType(PlaydateVisibilityType.PRIVATE);

        save(user);
        save(friend);
        save(notFriend);
        save(place);
        save(playdate);
        save(playdate2);
        save(playdate3);
        save(playdateWhoUserIsAttending);

        boolean b = PlaydateDAO.getInstance().addAttendance(user, playdateWhoUserIsAttending);
        assertTrue(b);

        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(user, friend);
        assertTrue(friendshipRequest.isPresent());
        Optional<Friendship> friendship = UserDAO.getInstance().createFriendship(friendshipRequest.get());
        assertTrue(friendship.isPresent());


        List<Playdate> playdatesWhoUserIsNotAttendingButCanAttendThroughFriend = PlaydateDAO.getInstance().getPlaydatesWhoUserIsNotAttendingButCanAttendThroughFriend(user);
        log.info("size of result = " + playdatesWhoUserIsNotAttendingButCanAttendThroughFriend.size());
        log.info("ids");
        for (Playdate s : playdatesWhoUserIsNotAttendingButCanAttendThroughFriend) {
            if (s == null) {
                log.info("playdate is null");
            } else {
                log.info("ID: " + playdate.getId() + " user is attending = " + user.getAttendingPlaydates().contains(s));
            }
        }
        log.info("user is attending the following playdates");
        assertTrue(playdatesWhoUserIsNotAttendingButCanAttendThroughFriend.size() == 1);
        assertEquals(playdate.getId(), playdatesWhoUserIsNotAttendingButCanAttendThroughFriend.get(0).getId());
        user.getAttendingPlaydates().forEach(playdate1 -> log.info("id: " + playdate1.getId()));

        remove(playdate);
        remove(playdate2);
        remove(playdate3);
        remove(playdateWhoUserIsAttending);
        remove(place);
        remove(user);
        remove(friend);
        remove(notFriend);
    }

}
