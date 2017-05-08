package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

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
        log.info("found user: " + userOptional.isPresent());
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

}
