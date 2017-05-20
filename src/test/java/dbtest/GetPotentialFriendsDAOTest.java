package dbtest;

import dblayer.InviteDAO;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
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
        boolean b = InviteDAO.getInstance().addInviteToUserAndPlaydate(invitedFriend, invite, playdate);
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
        boolean b = InviteDAO.getInstance().addInviteToUserAndPlaydate(attendingFriend, invite, playdate);
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
        boolean b = InviteDAO.getInstance().addInviteToUserAndPlaydate(attendingFriend, invite, playdate);
        assertTrue(b);

        Invite invite2 = new Invite(playdate, attendingFriend);
        boolean b2 = InviteDAO.getInstance().addInviteToUserAndPlaydate(invitedFriend, invite2, playdate);
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

        User invitedFriend = createUser();
        User attendingFriend = createUser();
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
            FriendshipRequest f = save(user, friend);
            save(f);
        }




        FriendshipRequest fr2 = save(user, attendingFriend);
        FriendshipRequest fr3 = save(user, invitedFriend);

        save(fr2);
        save(fr3);


        Invite invite = new Invite(playdate, attendingFriend);
        boolean b = InviteDAO.getInstance().addInviteToUserAndPlaydate(attendingFriend, invite, playdate);
        assertTrue(b);

        Invite invite2 = new Invite(playdate, attendingFriend);
        boolean b2 = InviteDAO.getInstance().addInviteToUserAndPlaydate(invitedFriend, invite2, playdate);
        assertTrue(b2);

        boolean b1 = PlaydateDAO.getInstance().acceptAndAddAttendance(invite);
        assertTrue(b1);


        List<User> potentialFriendsToInvite = PlaydateDAO.getInstance().getPotentialFriendsToInvite(playdate);
        log.info("found potential friends to invites = " + potentialFriendsToInvite.size());
        assertEquals(potentialFriendsToInvite.size(), 10);


        remove(playdate);
        remove(place);
        remove(user);
        notAttendingOrInvitedFriends.forEach(ModelCreators::remove);
        remove(attendingFriend);
        remove(invitedFriend);
    }




}
