package utils.fakedata;

import com.github.javafaker.Faker;
import dblayer.*;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;


import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static utils.Utils.unEscapeHTML;

@Slf4j
public class Data {

    public static Faker faker = new Faker();

    public static final String[] headers = new String[]{
            "Häng i", "Trevligheter i", "Nya vänner sökes vid"
    };

    private static int getRandomIntBetween(int start, int end) {
        return new Random().nextInt(end - start + 1) + start;
    }

    public static Optional<User> createSavedUserForInjectData() {
        User user = new User();
        user.setName(faker.name().firstName() + " " + faker.name().lastName());
        user.setGender(Gender.values()[faker.random().nextInt(2)]);
        user.setDescription("Jag heter " + unEscapeHTML(user.getName().split(" ")[0]) + " och är " + getRandomIntBetween(27, 40)
        + "år gammal. Jag är " + (user.getGender().equals(Gender.MALE) ? "pappa" : "mamma") + " till lilla " + faker.name().firstName()
        + ", " +faker.random().nextInt(4) + "år."
        );
        user.setEmail("" + getRandomIntBetween(0, 100) + faker.internet().emailAddress());
        user.setFacebookLinkUrl("http://facebook.com/profile.php?=73322363");
        user.setProfilePictureUrl(faker.internet().image(360, 360, false, ""));

        user.setFbToken(faker.internet().password(30, 60, true));
        user.setFacebookThirdPartyID(faker.internet().password(30, 60, true));
        user.setPhoneNumber(faker.phoneNumber().cellPhone());
        user.setAdmin(false);
        return UserDAO.getInstance().saveUserOnLogin(user);
    }

    public static void userCreatedPlaydateInNearFutureCloseToUser(User user, int[] grid) {
        List<Place> placeByLocation = PlaceDAO.getInstance().getPlaceByLocation(grid[0], grid[1]);
        if (placeByLocation.size() == 0) {
            log.info("no places at this position");
            return;
        }
        Place place = placeByLocation.get(getInInBoundOfCollection(placeByLocation));
        Optional<Playdate> savedPlaydate = createSavedPlaydate(user, place);
        if (!savedPlaydate.isPresent()) {
            log.info("unable to create playdate");
        }
    }

    private static void inviteFriendsToPlaydate(User user, Playdate playdate, double percentageToInvite) {
        if (percentageToInvite > 1) {
            log.info("cant invite more than 100%");
            return;
        }
        if (user.getFriends().size() == 0) {
            log.info("user have no friends");
            return;
        }
        int size = user.getFriends().size();
        int toInvite = (int) (percentageToInvite * size);
        if (toInvite != 0) {
            toInvite--;
        }
        List<User> friendsToInvite = getNumberOfRandomFriends(toInvite, user.getFriends());
        Collections.shuffle(friendsToInvite);
        int index = 0;
        while (index < size && toInvite >= 0){
            if (inviteFriendToPlaydate(playdate,friendsToInvite.get(index))) {
                toInvite--;
            }
            index++;
        }

        friendsToInvite.forEach(user1 -> inviteFriendToPlaydate(playdate, user1));
    }

    public static void createUsersWhoHavePlaydateWhereUserIsInvited(User user) {
        Optional<User> savedUserForInjectData = createSavedUserForInjectData();
        if (!savedUserForInjectData.isPresent()) {
            log.info("unable to create user");
            return;
        }

        createFriend(savedUserForInjectData.get());
        Optional<Place> randomPlace = getRandomPlace();
        if (!randomPlace.isPresent()) {
            log.info("unable to find random place");
            return;
        }
        Optional<Playdate> savedPlaydate = createSavedPlaydate(savedUserForInjectData.get(), randomPlace.get());
        if (!savedPlaydate.isPresent()) {
            log.info("unable to create playdate");
            return;
        }
        if (makeUserFriends(user, savedUserForInjectData.get())) {
            boolean b = inviteFriendToPlaydate(savedPlaydate.get(), user);
            if (!b) {
                log.info("unable to invite user");
            }
        }
    }

    private static boolean makeUserFriends(User user, User friend) {
        Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(user, friend);
        if (!friendshipRequest.isPresent()) {
            log.info("unable to create friendshiprequest");
            return false;
        }
        Optional<Friendship> friendship = UserDAO.getInstance().createFriendship(friendshipRequest.get());
        if (!friendship.isPresent()) {
            log.info("unable to create friendship");
            return false;
        }
        return true;
    }



    public static boolean inviteFriendToPlaydate(Playdate playdate, User friend) {
        Invite invite = new Invite(playdate, friend);
        boolean b = InviteDAO.getInstance().addInviteToUserAndPlaydate(invite);
        if (!b) {
            log.info("unable to send invite");
            return false;
        }
        return true;
    }


    public static List<User> getNumberOfRandomFriends(int number, Collection<Friendship> friends) {
        List<User> collect = friends.stream().map(Friendship::getFriend).collect(Collectors.toList());
        Collections.shuffle(collect);
        return collect.subList(0, number);
    }


    private static int getInInBoundOfCollection(Collection collection) {
        return faker.random().nextInt(collection.size() - 1);
    }


    public static Optional<Playdate> createSavedPlaydate(User owner, Place place) {
        Playdate playdate = new Playdate();
        playdate.setPlace(place);
        playdate.setOwner(owner);
        playdate.setPlaydateVisibilityType(PlaydateVisibilityType.values()[faker.random().nextInt(1)]);
        playdate.setStartTime(faker.date().future(15, TimeUnit.HOURS).getTime());
        playdate.setDescription("Jag tänkte gå till " + unEscapeHTML(place.getName()) + " och hänga lite med barnen," +
                "Skulle vara trevligt om någon annan vill hänga med. Stället ligger vid " + unEscapeHTML(place.getStreetAddress()
        ));

        playdate.setHeader(headers[faker.random().nextInt(2)] + " " + unEscapeHTML(place.getName()));
        return PlaydateDAO.getInstance().saveNewPlaydate(playdate);
    }


    public static void userCreatedPlaydateInNearFutureRandomPlace(User user) {
        Optional<Place> randomPlaceOpt = getRandomPlace();
        if (randomPlaceOpt.isPresent()) {
            Optional<Playdate> savedPlaydate = createSavedPlaydate(user, randomPlaceOpt.get());
            if (!savedPlaydate.isPresent()) {
                log.info("unable to create playdate");
            }
        } else {
            log.info("Unable to find random place");
        }
    }


    public static void userFriendCreatePlaydate(User user) {
        if (user.getFriends().size() == 0) {
            log.info("user has no friends, can't create playdate");
            return;
        }
        User friend = getFriendOfUser(user);
        if (friend == null) {
            log.info("got null friend");
            return;
        }
        userCreatedPlaydateInNearFutureRandomPlace(friend);
    }

    public static User getFriendOfUser(User user) {
        Set<Friendship> friends = user.getFriends();
        List<User> w = friends.stream().map(Friendship::getFriend).collect(Collectors.toList());
        return w.get(faker.random().nextInt(friends.size() - 2) + 1);
    }


    public static void createUserWhoHasSentFriendRequestToUser(User user) {
        Optional<User> savedUserForInjectData = createSavedUserForInjectData();
        if (savedUserForInjectData.isPresent()) {
            Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(savedUserForInjectData.get(), user);
            if (!friendshipRequest.isPresent()) {
                log.info("unable to create friendshipRequest");
            }
        } else {
            log.info("unable to create user");
        }
    }

    public static void createUserWhoUserSendFriendrequestTo(User user) {
        Optional<User> requestReciever = createSavedUserForInjectData();
        if (requestReciever.isPresent()) {
            Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(user, requestReciever.get());
            if (!friendshipRequest.isPresent()) {
                log.info("unable to create friendshiprequest");
            }
        } else {
            log.info("unable to create user");
        }
    }

    public static void createFriend(User user) {
        Optional<User> friend = createSavedUserForInjectData();
        if (friend.isPresent()) {
            Optional<FriendshipRequest> friendshipRequest = UserDAO.getInstance().createFriendshipRequest(friend.get(), user);
            if (friendshipRequest.isPresent()) {
                Optional<Friendship> friendship = UserDAO.getInstance().createFriendship(friendshipRequest.get());
                if (!friendship.isPresent()) {
                    log.info("unable to create friendship");
                }
            } else {
                log.info("unable to create friendship request");
            }
        }
    }


    public static Optional<Place> getRandomPlace() {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM Place ORDER BY RAND()", Place.class).setMaxResults(1).uniqueResultOptional();
        }
    }

}
