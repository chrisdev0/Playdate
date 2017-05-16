package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import utils.Utils;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ret;
    }


    @SuppressWarnings("Duplicates")
    public boolean saveUpdatedUser(User user) {
        boolean ret = false;
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.update(user);
            tx.commit();
            ret = true;
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

    /*** Hämtar alla vänner man har ***/
    public Optional<Set<User>> getFriendsOfUser(User user) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            session.refresh(user);
            Hibernate.initialize(user.getFriends().size());
            return Optional.of(user.getFriends()
                    .stream()
                    .map(Friendship::getRequester)
                    .collect(Collectors.toSet()));
        }
    }

    /*** Hämtar alla friendrequest man har ***/
    public Optional<Set<User>> getFriendRequest(User user) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            Hibernate.initialize(user.getFriendshipRequest());
            return Optional.of(user.getFriendshipRequest()
                    .stream()
                    .map(FriendshipRequest::getSender)
                    .collect(Collectors.toSet()));
        }
    }

    public int getFriendState(User thisUser, User otherUser) {
        if (thisUser.equals(otherUser)) {
            //inloggad användare är samma som användarens profil
            return 0;
        } else {
            if (checkIfFriendWithUser(thisUser.getId(), otherUser.getId()).isPresent()) {
                //inloggad användare är redan vän med användaren
                return 1;
            } else {
                Optional<FriendshipRequest> friendshipRequest = checkIfFriendRequestSent(otherUser.getId(), thisUser.getId());
                if (friendshipRequest.isPresent()) {
                    if (friendshipRequest.get().getReceiver().equals(thisUser)) {
                        //inloggad användare har fått friendshiprequest av användaren
                        return 2;
                    } else {
                        //inloggad användare har skickat friendshriprequest till användaren
                        return 3;
                    }
                } else {
                    //inloggad användare har inte fått friendshiprequest,
                    //är inte vänner
                    //och har inte skickat friendshriprequest
                    //(och användarna är inte samma)
                    return 4;
                }
            }
        }
    }

    /*** Hämtar alla användare man har skickat friendrequests till ***/
    public Optional<Set<User>> getSentFriendRequest(User user) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            Hibernate.initialize(user.getSentFriendshipRequest());
            return Optional.of(user.getSentFriendshipRequest()
                    .stream()
                    .map(FriendshipRequest::getReceiver)
                    .collect(Collectors.toSet()));
        }
    }

    /*** denna ska tas bort, finns en reda ***/
    @Deprecated
    public Optional<Set<User>> getFriendshipRequestOfUserTEMP(User user) {
        try (Session session = HibernateUtil.getInstance().openSession()){
            session.refresh(user);
            Set<User> collect = user.getFriendshipRequest().stream().map(FriendshipRequest::getSender).collect(Collectors.toSet());
            return Optional.of(collect);
        } catch (Exception e) {
            log.error("error getting friendshiprequests of user",e);
            return Optional.empty();
        }
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

    /*** tar in två användare och skapar en vänskapsförfrågan***/
    public Optional<FriendshipRequest> createFriendshipRequest(User sender, User friend){
        FriendshipRequest fr = new FriendshipRequest(sender, friend);
        friend.addFriendshipRequest(fr);
        sender.addSentFriendshipRequest(fr);

        if (checkIfFriendRequestSent(friend.getId(), sender.getId()).isPresent()){
            log.info("Friend request already sent");
            return Optional.empty();
        }

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

    /*** tar in två användarID och kontrollerar om det finns en vänskap mellan dem ***/
    public Optional<Friendship> checkIfFriendWithUser(Long userId, Long friendId){
        try(Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM Friendship WHERE friend.id = :user1 AND requester.id = :user2", Friendship.class)
                    .setParameter("user1", userId)
                    .setParameter("user2", friendId).uniqueResultOptional();
        }
    }

    /*** tar in två användarID och kontrollerar om det finns en obesvarad vänförfrågan mellan dem ***/
    public Optional<FriendshipRequest> checkIfFriendRequestSent(Long userId, Long friendId){
        try(Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM FriendshipRequest WHERE (sender.id = :user1 AND receiver.id = :user2) OR (sender.id = :user2 AND receiver.id = :user1)", FriendshipRequest.class)
                    .setParameter("user1", userId)
                    .setParameter("user2", friendId).uniqueResultOptional();
        }
    }

    /*** tar in två användare, kontrollerar att det finns en vänförfrågan mellan dem och tar bort den ***/
    public boolean declineFriendRequest(User user, User friend){
        Optional<FriendshipRequest> userFriendshipRequest = checkIfFriendRequestSent(user.getId(), friend.getId());

        if (!userFriendshipRequest.isPresent()){
            log.info("userFriendshipRequest finns ej");
            return false;
        }
        Session session = null;
        Transaction tx = null;
        boolean ret;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.remove(userFriendshipRequest.get());
            user.removeFriendshipRequest(userFriendshipRequest.get());
            session.update(user);
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

    /*** tar emot en vänskapsförfrågan, skapar en vänskap och tar bort förfrågan ***/
    public Optional<Friendship> createFriendship(FriendshipRequest friendshipRequest) {
        Optional<Friendship> friendship = checkIfFriendWithUser(friendshipRequest.getSender().getId(), friendshipRequest.getReceiver().getId());
        log.info("Vad är i friendship " + friendship.toString());
        if(friendship.isPresent()){
            log.info("Users are already friends");
            return Optional.empty();
        }

        User user = friendshipRequest.getSender();
        User friend = friendshipRequest.getReceiver();

        Friendship userFriendship = new Friendship(user, friend);
        Friendship friendFriendship = new Friendship(friend, user);
        Session session = null;
        Transaction tx = null;
        Optional<Friendship> ret = Optional.empty();

        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();

            if(checkIfFriendWithUser(user.getId(), friend.getId()).isPresent()) {
                log.error("Friendship already exists");
                throw halt(400);
            }

            friend.getFriendshipRequest().remove(friendshipRequest);
            user.addFriend(friend);
            friend.addFriend(user);
            session.delete(friendshipRequest);
            session.save(userFriendship);
            session.save(friendFriendship);
            session.update(user);
            session.update(friend);
            ret = Optional.of(userFriendship);
            tx.commit();
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
    /*** tar emot en vänskap och tar bort den  ***/
    public boolean deleteFriendship(Friendship friendship) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;

        User friend = friendship.getFriend();
        User user = friendship.getRequester();

        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            user.removeFriend(friendship);
            friend.removeFriend(friendship);
            session.update(friend);
            session.update(user);
            session.remove(friendship);
            tx.commit();
            ret = true;
        } catch (Exception e) {
            log.error("Error i delete friendship", e);
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


    public boolean removeFriendship(User user, User friend) {
        boolean ret = false;
        Session session;
        Transaction tx = null;
        try {
            Optional<Friendship> friendship1 = checkIfFriendWithUser(user.getId(), friend.getId());
            Optional<Friendship> friendship2 = checkIfFriendWithUser(friend.getId(), user.getId());

            if (friendship1.isPresent() && friendship2.isPresent()) {
                session = HibernateUtil.getInstance().openSession();
                tx = session.beginTransaction();
                session.delete(friendship1.get());
                session.delete(friendship2.get());
                session.update(user);
                session.update(friend);
                tx.commit();
                ret = true;
            }
        } catch (Exception e) {
            log.error("error removing friendship", e);
            if (tx != null) {
                tx.rollback();
            }
        }
        return ret;
    }

    public boolean removeOrDeclineFriendshipRequest(User user, User friend) {
        Optional<FriendshipRequest> friendshipRequest = checkIfFriendRequestSent(user.getId(), friend.getId());
        boolean ret = false;
        if (friendshipRequest.isPresent()) {
            Session session;
            Transaction tx = null;
            try {
                session = HibernateUtil.getInstance().openSession();
                tx = session.beginTransaction();
                session.remove(friendshipRequest.get());
                session.update(user);
                session.update(friend);
                tx.commit();
                ret = true;
            } catch (Exception e) {
                log.error("error removing friendshiprequest", e);
                if (tx != null) {
                    tx.rollback();
                }
            }
        }
        return ret;
    }

}
