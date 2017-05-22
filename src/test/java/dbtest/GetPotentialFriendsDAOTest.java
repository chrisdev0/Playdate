package dbtest;

import dblayer.InviteDAO;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.junit.Test;
import testutils.ModelCreators;
import testutils.TestStarter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static testutils.ModelCreators.*;

@Slf4j
public class GetPotentialFriendsDAOTest extends TestStarter {

    @Test
    public void testOnlyFriendDAO() {
        User user = createUser();
        User friend = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(user, place);
        save(user);
        save(friend);
        save(place);
        save(playdate);

        FriendshipRequest fr = save(user, friend);
        save(fr);

        List<User> potentialFriendsToInvite = PlaydateDAO.getInstance().getPotentialFriendsToInvite(playdate);
        log.info("found potential friends to invites = " + potentialFriendsToInvite.size());

        assertEquals(potentialFriendsToInvite.size(), 1);

        remove(playdate);
        remove(place);
        remove(user);
        remove(friend);
    }

    @Test
    public void testFriendAndOneFriendInvitedDAO() {
        User user = createUser();
        User friend = createUser();
        User invitedFriend = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(user, place);
        save(user);
        save(friend);
        save(invitedFriend);
        save(place);
        save(playdate);

        FriendshipRequest fr = save(user, friend);
        FriendshipRequest fr2 = save(user, invitedFriend);
        save(fr);
        save(fr2);


        Invite invite = new Invite(playdate, invitedFriend);
        boolean b = InviteDAO.getInstance().addInviteToUserAndPlaydate(invite);
        assertTrue(b);


        List<User> potentialFriendsToInvite = PlaydateDAO.getInstance().getPotentialFriendsToInvite(playdate);
        log.info("found potential friends to invites = " + potentialFriendsToInvite.size());
        assertEquals(potentialFriendsToInvite.size(), 1);


        remove(playdate);
        remove(place);
        remove(user);
        remove(invitedFriend);
        remove(friend);
    }

    @Test
    public void testFriendAndOneFriendAttendingDAO() {
        User user = createUser();
        User friend = createUser();
        User attendingFriend = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(user, place);
        save(user);
        save(attendingFriend);
        save(friend);
        save(place);
        save(playdate);

        FriendshipRequest fr = save(user, friend);
        FriendshipRequest fr2 = save(user, attendingFriend);
        save(fr);
        save(fr2);


        Invite invite = new Invite(playdate, attendingFriend);
        boolean b = InviteDAO.getInstance().addInviteToUserAndPlaydate(invite);
        assertTrue(b);

        boolean b1 = PlaydateDAO.getInstance().acceptAndAddAttendance(invite);
        assertTrue(b1);


        List<User> potentialFriendsToInvite = PlaydateDAO.getInstance().getPotentialFriendsToInvite(playdate);
        log.info("found potential friends to invites = " + potentialFriendsToInvite.size());
        assertEquals(potentialFriendsToInvite.size(), 1);


        remove(playdate);
        remove(place);
        remove(user);
        remove(friend);
        remove(attendingFriend);
    }



    @Test
    public void testFriendAndOneFriendAttendingOneFriendInvitedDAO() {
        User user = createUser();
        User friend = createUser();
        User invitedFriend = createUser();
        User attendingFriend = createUser();
        Place place = createPlace();
        Playdate playdate = createPlaydate(user, place);
        save(user);
        save(attendingFriend);
        save(invitedFriend);
        save(friend);
        save(place);
        save(playdate);

        FriendshipRequest fr = save(user, friend);
        FriendshipRequest fr2 = save(user, attendingFriend);
        FriendshipRequest fr3 = save(user, invitedFriend);
        save(fr);
        save(fr2);
        save(fr3);


        Invite invite = new Invite(playdate, attendingFriend);
        boolean b = InviteDAO.getInstance().addInviteToUserAndPlaydate(invite);
        assertTrue(b);

        Invite invite2 = new Invite(playdate, invitedFriend);
        boolean b2 = InviteDAO.getInstance().addInviteToUserAndPlaydate(invite2);
        assertTrue(b2);

        boolean b1 = PlaydateDAO.getInstance().acceptAndAddAttendance(invite);
        assertTrue(b1);


        List<User> potentialFriendsToInvite = PlaydateDAO.getInstance().getPotentialFriendsToInvite(playdate);
        log.info("found potential friends to invites = " + potentialFriendsToInvite.size());
        assertEquals(potentialFriendsToInvite.size(), 1);


        remove(playdate);
        remove(place);
        remove(user);
        remove(friend);
        remove(attendingFriend);
        remove(invitedFriend);
    }


    @Test
    public void testMultiFriendAndOneFriendAttendingOneFriendInvitedDAO() {
        List<User> notAttendingOrInvitedFriends = new ArrayList<>();
        User user = createUser();
        user.setName("ÄGARE");
        User invitedFriend = createUser();
        invitedFriend.setName("INBJUDEN VÄN");
        User attendingFriend = createUser();
        attendingFriend.setName("DELTAGANDE VÄN");
        Place place = createPlace();
        Playdate playdate = createPlaydate(user, place);
        save(user);
        save(attendingFriend);
        save(invitedFriend);

        save(place);
        save(playdate);

        for(int i = 0; i < 10; i++) {
            User friend = createUser();
            notAttendingOrInvitedFriends.add(friend);
            save(friend);
            friend.setName("VÄNSOMSKARETURNERAS");
            FriendshipRequest f = save(user, friend);
            save(f);
        }




        FriendshipRequest fr2 = save(user, attendingFriend);
        FriendshipRequest fr3 = save(user, invitedFriend);

        save(fr2);
        save(fr3);


        Invite invite = new Invite(playdate, attendingFriend);
        boolean b = InviteDAO.getInstance().addInviteToUserAndPlaydate(invite);
        assertTrue(b);

        Invite invite2 = new Invite(playdate, invitedFriend);
        boolean b2 = InviteDAO.getInstance().addInviteToUserAndPlaydate(invite2);
        assertTrue(b2);

        boolean b1 = PlaydateDAO.getInstance().acceptAndAddAttendance(invite);
        assertTrue(b1);


        List<User> potentialFriendsToInvite = PlaydateDAO.getInstance().getPotentialFriendsToInvite(playdate);
        log.info("found potential friends to invites = " + potentialFriendsToInvite.size());
        potentialFriendsToInvite.forEach(user1 -> {
            log.info("user id =" + user1.getName());
        });
        assertEquals(potentialFriendsToInvite.size(), 10);


        remove(playdate);
        remove(place);
        remove(user);
        notAttendingOrInvitedFriends.forEach(ModelCreators::remove);
        remove(attendingFriend);
        remove(invitedFriend);
    }




}
