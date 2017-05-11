package util;

import com.github.javafaker.Faker;
import dblayer.PlaceDAO;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
import model.*;
import testhelpers.HibernateTests;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class ModelCreators {


    private static Faker faker = new Faker();

    public static User createUser() {
        User user = new User();
        user.setName(faker.name().name());
        user.setEmail(faker.internet().emailAddress());
        user.setFbToken(faker.internet().password(19, 20, true, true));
        user.setGender(Gender.OTHER);
        user.setDescription("Testanvändare");
        user.setFacebookThirdPartyID(faker.internet().password(19, 20, true, true));
        user.setProfilePictureUrl(faker.internet().image());
        return user;
    }


    public static Place createPlace() {
        Place place = new Place();
        place.setLongDescription(faker.lorem().characters(300));
        place.setShortDescription(faker.lorem().characters(120, 300));
        place.setTimeUpdated(faker.date().past(200, TimeUnit.DAYS).toString());
        place.setTimeCreated(faker.date().past(300, TimeUnit.DAYS).toString());
        place.setName(faker.address().streetName() + " " + faker.address().citySuffix());
        place.setGeoY(6000 + faker.random().nextInt(16000));
        place.setGeoX(6000 + faker.random().nextInt(16000));
        place.setGeoArea("TestArea");
        place.setSthlmAPIid(faker.internet().password(19, 20, true, true));
        place.setImageId(faker.internet().password(19, 20, true, true));
        place.setCityAddress(faker.address().cityName());
        place.setZip(faker.address().zipCode());
        place.setCategory("TestPlats");
        place.setStreetAddress(faker.address().streetAddress());
        place.setTimeUpdated("123");
        return place;
    }

    public static Playdate createPlaydate(User user, Place place) {
        Playdate playdate = new Playdate();
        playdate.setPlace(place);
        playdate.setOwner(user);
        playdate.setStartTime(faker.date().future(200, TimeUnit.HOURS).getTime());
        playdate.setHeader("TestPlaydate");
        playdate.setDescription(faker.lorem().characters(300));
        playdate.setPlaydateVisibilityType(PlaydateVisibilityType.PUBLIC);
        return playdate;
    }

    public static void remove(User user) {
        boolean b = UserDAO.getInstance().deleteUser(user);
        assertTrue(b);
    }

    public static void remove(Invite invite) {
        boolean b = PlaydateDAO.getInstance().removeInvite(invite, invite.getPlaydate(), invite.getInvited());
        assertTrue(b);
    }

    public static void remove(Place place) {
        boolean b = PlaceDAO.getInstance().deletePlaceById(place.getId());
        assertTrue(b);
    }

    public static void remove(Playdate playdate) {
        boolean b = PlaydateDAO.getInstance().deletePlaydate(playdate);
        assertTrue(b);
    }

    public static void remove(User user, User friend){
        boolean b = UserDAO.getInstance().declineFriendRequest(user, friend);
        assertTrue(b);
    }

    public static void save(User user) {
        boolean b = UserDAO.getInstance().saveUserOnLogin(user).isPresent();
        assertTrue(b);
    }

    public static void save(Place place) {
        boolean b = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b);
    }

    public static void save(Playdate playdate) {
        Optional<Playdate> playdateOptional = PlaydateDAO.getInstance().saveNewPlaydate(playdate);
        assertTrue(playdateOptional.isPresent());
    }

    public static void save(Invite invite, User user, Playdate playdate) {
        boolean b = PlaydateDAO.getInstance().addInviteToUserAndPlaydate(user, invite, playdate);
        assertTrue(b);
    }

    public static FriendshipRequest save(User user, User friend){
        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(user, friend);
        assertTrue(friendshipRequest.isPresent());
        return friendshipRequest.get();
    }

    public static void save(FriendshipRequest friendshipRequest) {
        boolean b = UserDAO.getInstance().createFriendship(friendshipRequest).isPresent();
        assertTrue(b);
    }

}
