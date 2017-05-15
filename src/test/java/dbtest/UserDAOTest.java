package dbtest;

import com.sun.tools.internal.xjc.model.Model;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import testhelpers.HibernateTests;
import testutils.ModelCreators;

import static org.junit.Assert.*;

import java.util.Optional;
import java.util.Set;

@Slf4j
public class UserDAOTest extends HibernateTests {


    @Test
    public void testSaveUser() {
        User user = ModelCreators.createUser();
        boolean saveok = UserDAO.getInstance().saveUserOnLogin(user).isPresent();
        assertNotNull(user.getId());
        boolean deleteUser = UserDAO.getInstance().deleteUser(user);
        assertTrue(deleteUser);
    }

    @Test
    public void testSaveIllegalUser() {
        User user = ModelCreators.createUser();
        user.setEmail(null);
        boolean saveOk = UserDAO.getInstance().saveUserOnLogin(user).isPresent();
        assertFalse(saveOk);

        boolean b = UserDAO.getInstance().deleteUser(user);
        assertFalse(b);
    }

    @Test
    public void testSaveIllegalUser2() {
        User user = ModelCreators.createUser();
        user.setName(null);
        boolean saveOk = UserDAO.getInstance().saveUserOnLogin(user).isPresent();
        assertFalse(saveOk);

        boolean b = UserDAO.getInstance().deleteUser(user);
        assertFalse(b);
    }

    @Test
    public void testOnUpdateSaveUser() {
        User user = ModelCreators.createUser();
        boolean b = UserDAO.getInstance().saveUserOnLogin(user).isPresent();
        assertTrue(b);
        user.setFbToken(faker.internet().password(19, 20, true, true));
        boolean save2 = UserDAO.getInstance().saveUserOnLogin(user).isPresent();
        assertTrue(save2);

        boolean b1 = UserDAO.getInstance().deleteUser(user);
        assertTrue(b1);
    }


    @Test
    public void testDeleteUserTwice() {
        User user = ModelCreators.createUser();
        boolean saveOk = UserDAO.getInstance().saveUserOnLogin(user).isPresent();
        assertTrue(saveOk);
        assertNotNull(user.getId());
        boolean deleteUser = UserDAO.getInstance().deleteUser(user);
        assertTrue(deleteUser);
        boolean deleteUser2 = UserDAO.getInstance().deleteUser(user);
        assertFalse(deleteUser2);
    }

    @Test
    public void testGetUserById() {
        User user = ModelCreators.createUser();
        boolean b = UserDAO.getInstance().saveUserOnLogin(user).isPresent();
        assertTrue(b);
        assertNotNull(user.getId());

        Optional<User> userById = UserDAO.getInstance().getUserById(user.getId());
        assertTrue(userById.isPresent());

        assertEquals(user.getEmail(), userById.get().getEmail());

        boolean deleteUser = UserDAO.getInstance().deleteUser(user);
        assertTrue(deleteUser);
    }

    @Test
    public void testSaveNullImage() {
        ProfilePicture profilePicture = new ProfilePicture();
        User user = ModelCreators.createUser();
        ModelCreators.save(user);
        Optional<Long> aLong = UserDAO.getInstance().saveImageToDB(profilePicture, user);
        assertFalse(aLong.isPresent());

        ModelCreators.remove(user);
    }

    @Test
    public void testSaveProfilePicture() {
        byte[] image = new byte[10];
        for(int i = 0; i < image.length; i++) {
            image[i] = (byte) (Math.random() * 10);
        }
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setImage(image);

        User user = ModelCreators.createUser();
        ModelCreators.save(user);

        Optional<Long> imageOpt = UserDAO.getInstance().saveImageToDB(profilePicture, user);

        assertTrue(imageOpt.isPresent());
        assertNotNull(profilePicture.getId());

        Optional<ProfilePicture> profilePictureOfUser = UserDAO.getInstance().getProfilePictureOfUser(imageOpt.get());

        assertTrue(profilePictureOfUser.isPresent());

        assertArrayEquals(image, ArrayUtils.toPrimitive(profilePictureOfUser.get().getImage()));

        boolean b = UserDAO.getInstance().deleteProfilePicture(profilePicture);
        assertTrue(b);
        ModelCreators.remove(user);
    }

    @Test
    public void testDeleteProfilePictureTwice() {
        byte[] image = new byte[10];
        for(int i = 0; i < image.length; i++) {
            image[i] = (byte) (Math.random() * 10);
        }
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setImage(image);

        User user = ModelCreators.createUser();
        ModelCreators.save(user);

        Optional<Long> imageOpt = UserDAO.getInstance().saveImageToDB(profilePicture, user);

        assertTrue(imageOpt.isPresent());
        assertNotNull(profilePicture.getId());
        ModelCreators.remove(user);
    }

    @Test
    public void testGetUserByEmail() {
        User user = ModelCreators.createUser();
        String thirdPartyAPIID = user.getFacebookThirdPartyID();
        log.info(user.getEmail());
        boolean saveUserOnLogin = UserDAO.getInstance().saveUserOnLogin(user).isPresent();
        assertTrue(saveUserOnLogin);
        Optional<User> userByEmail = UserDAO.getInstance().getUserByThirdPartyAPIID(thirdPartyAPIID);
        assertTrue(userByEmail.isPresent());

        assertEquals(user.getId(), userByEmail.get().getId());


        boolean d = UserDAO.getInstance().deleteUser(user);
        assertTrue(d);
    }

    @Test
    public void testSaveDuplicateUserEmail() {
        User user1 = ModelCreators.createUser();
        User user2 = ModelCreators.createUser();
        user1.setEmail("test@testtest.com");
        user2.setEmail("test@testtest.com");
        session.save(user1);
        session.save(user2);
        boolean fail = false;
        try {
            session.getTransaction().commit();

        } catch (Exception e) {
            log.error("save duplicate", e);
            fail = true;
        }
        session.beginTransaction();
        session.remove(user1);
        session.getTransaction().commit();
        assertTrue(fail);
    }

    @Test
    public void testSendFriendRequest(){
        User user = ModelCreators.createUser();
        User friend = ModelCreators.createUser();

        ModelCreators.save(user);
        ModelCreators.save(friend);
        assertTrue(UserDAO.getInstance().createFriendshipRequest(user, friend).isPresent());
    }

    @Test
    public void testFriendRequestAlreadySent(){
        User user = ModelCreators.createUser();
        User friend = ModelCreators.createUser();

        ModelCreators.save(user);
        ModelCreators.save(friend);

        FriendshipRequest fr = new FriendshipRequest(user, friend);
        Set<FriendshipRequest> friendshipRequestSet = friend.getFriendshipRequest();
        ModelCreators.save(user, friend);
        friendshipRequestSet.add(fr);


        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().checkIfFriendRequestSent(user.getId(), friend.getId());
        assertTrue(friendshipRequest.isPresent());
        ModelCreators.remove(user, friend);
        ModelCreators.remove(friend);
        ModelCreators.remove(user);
    }

    @Test
    public void testDeclineFriendRequest(){
        User user = ModelCreators.createUser();
        User friend = ModelCreators.createUser();

        ModelCreators.save(user);
        ModelCreators.save(friend);

        Optional<FriendshipRequest> fr = UserDAO.getInstance().createFriendshipRequest(user, friend);
        assertTrue(fr.isPresent());
        assertTrue(UserDAO.getInstance().declineFriendRequest(user, friend));
        ModelCreators.remove(user);
        ModelCreators.remove(friend);

    }

    @Test
    public void testDeclineNonExistingFriendRequest(){
        User user = ModelCreators.createUser();
        User friend = ModelCreators.createUser();

        ModelCreators.save(user);
        ModelCreators.save(friend);

        assertFalse(UserDAO.getInstance().checkIfFriendWithUser(user.getId(), friend.getId()).isPresent());

        assertFalse(UserDAO.getInstance().declineFriendRequest(user, friend));
        ModelCreators.remove(user);
        ModelCreators.remove(friend);

    }

    @Test
    public void testAddFriendship(){
        User user = ModelCreators.createUser();
        User friend = ModelCreators.createUser();

        ModelCreators.save(user);
        ModelCreators.save(friend);

        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(user, friend);
        assertTrue(friendshipRequest.isPresent());
        assertTrue(UserDAO.getInstance().createFriendship(friendshipRequest.get()).isPresent());

        UserDAO.getInstance().deleteFriendship(UserDAO.getInstance().checkIfFriendWithUser(user.getId(), friend.getId()).get());
        ModelCreators.remove(user);
        ModelCreators.remove(friend);
    }

    @Test
    public void testRemoveFriendship(){
        User user = ModelCreators.createUser();
        User friend = ModelCreators.createUser();

        ModelCreators.save(user);
        ModelCreators.save(friend);

        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(user, friend);

        Optional<Friendship> friendship = UserDAO.getInstance().createFriendship(friendshipRequest.get());
        assertTrue(friendship.isPresent());


        assertTrue(UserDAO.getInstance().deleteFriendship(friendship.get()));
        ModelCreators.remove(user);
        ModelCreators.remove(friend);

    }

    @Test
    public void testAddFriendshipAlreadyFriends(){
        User user = ModelCreators.createUser();
        User friend = ModelCreators.createUser();

        ModelCreators.save(user);
        ModelCreators.save(friend);

        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(user, friend);

        Optional<Friendship> friendship = UserDAO.getInstance().createFriendship(friendshipRequest.get());
        assertTrue(friendship.isPresent());

        assertFalse(UserDAO.getInstance().createFriendship(friendshipRequest.get()).isPresent());

        assertTrue(UserDAO.getInstance().deleteFriendship(friendship.get()));
        ModelCreators.remove(user);
        ModelCreators.remove(friend);
    }

    @Test
    public void testGetFriends(){
        User user = ModelCreators.createUser();
        User friend1 = ModelCreators.createUser();
        User friend2 = ModelCreators.createUser();
        User friend3 = ModelCreators.createUser();

        ModelCreators.save(user);
        ModelCreators.save(friend1);
        ModelCreators.save(friend2);
        ModelCreators.save(friend3);

        FriendshipRequest friendshipRequest1 = ModelCreators.save(user, friend1);
        FriendshipRequest friendshipRequest2 = ModelCreators.save(user, friend2);
        FriendshipRequest friendshipRequest3 = ModelCreators.save(user, friend3);

        ModelCreators.save(friendshipRequest1);
        ModelCreators.save(friendshipRequest2);
        ModelCreators.save(friendshipRequest3);

        assertTrue(UserDAO.getInstance().getFriendsOfUser(user).isPresent());

        ModelCreators.remove(user);
        ModelCreators.remove(friend1);
        ModelCreators.remove(friend2);
        ModelCreators.remove(friend3);

    }

    @Test
    public void testGetFriendRequest(){
        User user = ModelCreators.createUser();
        User requester1 = ModelCreators.createUser();
        User requester2 = ModelCreators.createUser();
        User requester3 = ModelCreators.createUser();

        ModelCreators.save(user);
        ModelCreators.save(requester1);
        ModelCreators.save(requester2);
        ModelCreators.save(requester3);

        ModelCreators.save(requester1, user);
        ModelCreators.save(requester2, user);
        ModelCreators.save(requester3, user);

        assertTrue(UserDAO.getInstance().getFriendRequest(user).isPresent());

        ModelCreators.remove(user);
        ModelCreators.remove(requester1);
        ModelCreators.remove(requester2);
        ModelCreators.remove(requester3);

    }

    @Test
    public void testGetSentFriendRequest(){
        User requester = ModelCreators.createUser();
        User receiver1 = ModelCreators.createUser();
        User receiver2 = ModelCreators.createUser();
        User receiver3 = ModelCreators.createUser();

        ModelCreators.save(requester);
        ModelCreators.save(receiver1);
        ModelCreators.save(receiver2);
        ModelCreators.save(receiver3);

        ModelCreators.save(requester, receiver1);
        ModelCreators.save(requester, receiver2);
        ModelCreators.save(requester, receiver3);

        assertTrue(UserDAO.getInstance().getSentFriendRequest(requester).isPresent());

        ModelCreators.remove(requester);
        ModelCreators.remove(receiver1);
        ModelCreators.remove(receiver2);
        ModelCreators.remove(receiver3);


    }

}