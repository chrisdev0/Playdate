package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import utils.Utils;
import java.util.Optional;
import java.util.Set;

import static spark.Spark.halt;

@Slf4j
public class UserDAO {
    private static UserDAO instance;


    private UserDAO(){

    }

    public static UserDAO getInstance(){
        if (instance == null){
            instance = new UserDAO();
        }
        return instance;
    }


    public Optional<User> getUserById(Long userId){
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.byId(User.class).loadOptional(userId);
        } catch (Exception e) {
            log.error("error getting user: ", e);
        }
        return Optional.empty();
    }

    public Optional<User> saveOrUpdateUserOnLogin(FacebookProfile facebookProfile) {
        if (Utils.isNotNullAndNotEmpty(facebookProfile.getDisplayName(), facebookProfile.getEmail(),
                facebookProfile.getThirdPartyId(), facebookProfile.getId())) {
            User user = new User(facebookProfile.getDisplayName(), facebookProfile.getEmail());
            user.setGender(Gender.genderFromFacebookGender(facebookProfile.getGender()));
            user.setFbToken(facebookProfile.getAccessToken());
            user.setFacebookThirdPartyID(facebookProfile.getThirdPartyId());
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> getUserByThirdPartyAPIID(String thirdPartyAPIID) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM User WHERE facebookThirdPartyID = :thirdparty", User.class)
                    .setParameter("thirdparty", thirdPartyAPIID).uniqueResultOptional();
        }
    }

    public Optional<User> saveUserOnLogin(User user) {
        Optional<User> ret = Optional.empty();
        Session session = null;
        Transaction tx = null;
        Optional<User> userOptional = getUserByThirdPartyAPIID(user.getFacebookThirdPartyID());
        log.info("found user via thirdparty: " + userOptional.isPresent());
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            if (userOptional.isPresent()) {
                User user1 = userOptional.get();
                user1.setFbToken(user.getFbToken());
                session.update(user1);
                ret = Optional.of(user1);
            } else {
                session.save(user);
                ret = Optional.of(user);
            }
            tx.commit();
        } catch (Exception e) {
            log.error("error saving user on login", e);
            if (tx != null) {
                tx.rollback();
            }
            ret = Optional.empty();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ret;
    }


    public Optional<Long> saveImageToDB(ProfilePicture profilePicture) {
        Optional<Long> ret = Optional.empty();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            Long id = (Long) session.save(profilePicture);
            tx.commit();
            ret = Optional.of(id);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("Error saving image", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ret;
    }


    @SuppressWarnings("Duplicates")
    public boolean deleteProfilePicture(ProfilePicture profilePicture) {
        boolean ret = false;
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.remove(profilePicture);
            tx.commit();
            ret = true;
        } catch (Exception e) {
            log.error("error in delete profile picture", e);
            if (tx != null) {
                tx.rollback();
            }
        }
        return ret;
    }


    @SuppressWarnings("Duplicates")
    public boolean deleteUser(User user) {
        boolean ret = false;
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.remove(user);
            tx.commit();
            ret = true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
        return ret;
    }


    public Optional<ProfilePicture> getProfilePictureOfUser(Long id) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.byId(ProfilePicture.class).loadOptional(id);
        }
    }

    public Optional<FriendshipRequest> createFriendshipRequest(User sender, User friend){
        FriendshipRequest fr = new FriendshipRequest(sender, friend);
        friend.addFriendshipRequeset(fr);

        Transaction tx = null;
        Session session = null;
        Optional<FriendshipRequest> ret = Optional.empty();
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.save(fr);
            session.update(friend);
            tx.commit();
            ret = Optional.of(fr);

        } catch (Exception e) {
            log.error("Exception i spara friendshipRequest ", e);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ret;
    }


    public Optional<Friendship> checkIfFriendWithUser(Long userId, Long friendId){
        /*
        Kolla i tabellerna om vänner

        Returnera sann om får tillbaka?
         */

        try(Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM Friendship WHERE friend.id = :user1 AND requester.id = :user2", Friendship.class)
                    .setParameter("user1", userId)
                    .setParameter("user2", friendId).uniqueResultOptional();

        }
    }

    public Optional<FriendshipRequest> checkIfFriendRequestSent(Long userId, Long friendId){
        /*
        Kolla i tabellerna om vänner

        Returnera sann om får tillbaka?
         */

        try(Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM FriendshipRequest WHERE (sender.id = :user1 AND receiver.id = :user2) OR (sender.id = :user2 AND receiver.id = :user1)", FriendshipRequest.class)
                    .setParameter("user1", userId)
                    .setParameter("user2", friendId).uniqueResultOptional();

        }
    }

    public boolean declineFriendRequest(User user, User friend){
        /*
        Om tackar nej, ta bort från friendRequest-listan och databasen
         */

        Optional<FriendshipRequest> userFriendshipRequest = checkIfFriendRequestSent(user.getId(), friend.getId());
        //Optional<FriendshipRequest> friendFriendshipRequest = checkIfFriendRequestSent(friend.getId(), user.getId());

        if (!userFriendshipRequest.isPresent()){
            log.info("userFriendshipRequest finns ej");
            return false;
        }
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.remove(userFriendshipRequest.get());
            //session.remove(friendFriendshipRequest);
            tx.commit();
            ret = true;
        }
        catch(Exception e){
            if (tx != null) {
                tx.rollback();
            }
            ret = false;
        }
        finally{
            if (session != null){
                session.close();
            }
        }
        return ret;
    }


    public boolean createFriendship(User user, User friend) {
        Friendship friendshipOfUser = new Friendship(friend, user);
        Friendship friendshipOfFriend = new Friendship(user, friend);
        Set<Friendship> friendsOfUser = user.getFriends();
        Set<Friendship> friendsOfFriend = friend.getFriends();
        Session session = null;
        Transaction tx = null;
        boolean ret = false;

        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();

            if(!friend.getFriendshipRequest().contains(user)) {
               log.error("No friendship request exists");
               throw halt(400);
            }

            if(!checkIfFriendWithUser(user.getId(), friend.getId()).isPresent()) {
                log.error("Friendship does not exist");
                throw halt(400);
            }

            friend.getFriendshipRequest().remove(user);
            friendsOfUser.add(friendshipOfUser);
            friendsOfFriend.add(friendshipOfFriend);

            session.save(friendshipOfUser);
            session.save(friendshipOfFriend);
            session.update(user);
            session.update(friend);
            tx.commit();
            ret = true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ret;
    }

    public boolean deleteFriendship(User user, User friend) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();

            Set<Friendship> friendsOfUser = user.getFriends();
            Set<Friendship> friendsOfFriend = friend.getFriends();

            if(!checkIfFriendWithUser(user.getId(), friend.getId()).isPresent()){
                log.error("friendship does not exist");
                throw halt(400);
            }

            if(!(friendsOfUser.contains(friend) && friendsOfFriend.contains(user))){
                log.error("friendship does not exist");
                throw halt(400);
            }

            Friendship friendFriendWithUser = checkIfFriendWithUser(user.getId(), friend.getId()).get();
            Friendship userFriendWithFriend = checkIfFriendWithUser(friend.getId(), user.getId()).get();

            friendsOfUser.remove(friend);
            friendsOfFriend.remove(user);

            session.remove(friendFriendWithUser);
            session.remove(userFriendWithFriend);
            session.update(friend);
            session.update(user);
            tx.commit();
            ret = true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ret;
    }

}
