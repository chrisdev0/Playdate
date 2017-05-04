package dblayer;

import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import utils.Utils;

import java.util.Optional;


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

    public Optional<User> getUserByEmail(String email) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM User WHERE UPPER(email) = :email", User.class)
                    .setParameter("email", email.toUpperCase()).uniqueResultOptional();
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
            userOptional.ifPresent(user1 -> user.setId(user1.getId()));
            session.saveOrUpdate(user);
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
