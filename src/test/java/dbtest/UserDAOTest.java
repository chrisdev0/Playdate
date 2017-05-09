package dbtest;

import apilayer.handlers.FriendshipHandler;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import testhelpers.HibernateTests;
import util.ModelCreators;

import javax.jws.WebParam;

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
        Optional<Long> aLong = UserDAO.getInstance().saveImageToDB(profilePicture);
        assertFalse(aLong.isPresent());
    }

    @Test
    public void testSaveProfilePicture() {
        byte[] image = new byte[10];
        for(int i = 0; i < image.length; i++) {
            image[i] = (byte) (Math.random() * 10);
        }
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setImage(image);

        Optional<Long> imageOpt = UserDAO.getInstance().saveImageToDB(profilePicture);

        assertTrue(imageOpt.isPresent());
        assertNotNull(profilePicture.getId());

        Optional<ProfilePicture> profilePictureOfUser = UserDAO.getInstance().getProfilePictureOfUser(imageOpt.get());

        assertTrue(profilePictureOfUser.isPresent());

        assertArrayEquals(image, ArrayUtils.toPrimitive(profilePictureOfUser.get().getImage()));

        boolean b = UserDAO.getInstance().deleteProfilePicture(profilePicture);
        assertTrue(b);
    }


    @Test
    public void testDeleteProfilePictureTwice() {
        byte[] image = new byte[10];
        for(int i = 0; i < image.length; i++) {
            image[i] = (byte) (Math.random() * 10);
        }
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setImage(image);

        Optional<Long> imageOpt = UserDAO.getInstance().saveImageToDB(profilePicture);

        assertTrue(imageOpt.isPresent());
        assertNotNull(profilePicture.getId());
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
    public void testSendFriendrequest(){
        User user = ModelCreators.createUser();
        User friend = ModelCreators.createUser();

        ModelCreators.save(user);
        ModelCreators.save(friend);
        assertTrue(UserDAO.getInstance().createFriendshipRequest(user, friend));
    }

    @Test
    public void testFriendRequestAlreadySent(){
        User user = ModelCreators.createUser();
        User friend = ModelCreators.createUser();

        ModelCreators.save(user);
        log.info(user.getName() + " " + user.getEmail());
        ModelCreators.save(friend);

        FriendshipRequest fr = new FriendshipRequest(user, friend);
        Set<FriendshipRequest> friendshipRequestSet = friend.getFriendshipRequest();
        ModelCreators.save(fr, user, friend);
        friendshipRequestSet.add(fr);


        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().checkIfFriendRequestSent(user.getId(), friend.getId());
        assertTrue(friendshipRequest.isPresent());

    }

}
