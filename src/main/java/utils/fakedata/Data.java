package utils.fakedata;

import com.github.javafaker.Faker;
import dblayer.HibernateUtil;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;


import java.util.Optional;
import java.util.Random;

@Slf4j
public class Data {

    public static Faker faker = new Faker();

    private static int getRandomIntBetween(int start, int end) {
        return new Random().nextInt(end - start + 1) + start;
    }

    public static User createSavedUserForInjectData() {
        User user = new User();
        user.setName(faker.name().firstName() + " " + faker.name().lastName());
        user.setDescription("Jag heter " + user.getName().split(" ")[0] + " och är " + getRandomIntBetween(27, 40));
        user.setEmail("" + getRandomIntBetween(0, 100) + faker.internet().emailAddress());
        user.setFacebookLinkUrl("http://facebook.com/profile.php?=73322363");
        user.setProfilePictureUrl(faker.internet().image(360, 360, false, ""));

        user.setFbToken(faker.internet().password(30, 60, true));
        user.setFacebookThirdPartyID(faker.internet().password(30, 60, true));
        user.setPhoneNumber(faker.phoneNumber().cellPhone());
        user.setGender(Gender.values()[faker.random().nextInt(2)]);
        user.setAdmin(false);
        UserDAO.getInstance().saveUserOnLogin(user);
        return user;
    }

    public static void userCreatedPlaydateInNearFutureCloseToUser(User user, int[] grid) {

    }

    public static void userCreatedPlaydateInNearFutureRandomPlace(User user) {
        Optional<Place> randomPlaceOpt = getRandomPlace();
        if (randomPlaceOpt.isPresent()) {
        } else {
            log.info("Unable to find random place");
        }
    }


    public static void createUserWhoHasSentFriendRequestToUser(User user) {
        User friendRequestSender = createSavedUserForInjectData();
        UserDAO.getInstance().createFriendshipRequest(friendRequestSender, user);
    }

    public static void createUserWhoUserSendFriendrequestTo(User user) {
        User friendRequestReciever = createSavedUserForInjectData();
        UserDAO.getInstance().createFriendshipRequest(user, friendRequestReciever);
    }

    public static void createFriend(User user) {
        User friend = createSavedUserForInjectData();
        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(friend, user);
        if (friendshipRequest.isPresent()) {
            Optional<Friendship> friendship = UserDAO.getInstance().createFriendship(friendshipRequest.get());
            if (!friendship.isPresent()) {
                log.info("unable to create friendship");
            }
        } else {
            log.info("unable to create friendship request");
        }
    }

    public static Optional<Place> getRandomPlace() {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM Place ORDER BY RAND()", Place.class).setMaxResults(1).uniqueResultOptional();
        }
    }

}
