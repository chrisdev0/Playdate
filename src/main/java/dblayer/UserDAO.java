package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import utils.Utils;

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
    public Optional<User> getUserById(Long friendId){
        try(Session session = HibernateUtil.getInstance().openSession()){
            return session.byId(User.class).loadOptional(friendId);
        }
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

    public Optional<User> getUserByEmail(String thirdPartyAPIID) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM User WHERE facebookThirdPartyID = :thirdparty", User.class)
                    .setParameter("thirdparty", thirdPartyAPIID).uniqueResultOptional();
        }
    }

    public boolean saveUserOnLogin(User user) {
        boolean ret = false;
        Session session = null;
        Transaction tx = null;
        Optional<User> userOptional = getUserByEmail(user.getEmail());
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            if (userOptional.isPresent()) {
                User user1 = userOptional.get();
                user1.setFbToken(user.getFbToken());
                session.update(user1);
            } else {
                session.save(user);
            }
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


    public Optional<ProfilePicture> getProfilePictureOfUser(Long id) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.byId(ProfilePicture.class).loadOptional(id);
        }
    }

}
