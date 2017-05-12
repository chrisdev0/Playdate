package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.Invite;
import model.Playdate;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class InviteDao {

    private static InviteDao instance;

    private InviteDao(){}

    public static InviteDao getInstance() {
        if (instance == null) {
            instance = new InviteDao();
        }
        return instance;
    }

    public boolean addInviteToUserAndPlaydate(User user, Invite invite, Playdate playdate) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.update(playdate);
            session.update(user);
            session.save(invite);

            playdate.addInvite(invite);
            user.addInvite(invite);

            tx.commit();
            ret = true;
        } catch (Exception e) {
            log.error("error adding invite", e);
            if (tx != null) {
                tx.rollback();
            }
            ret = false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ret;
    }


    public Optional<List<Invite>> getInvitesOfUser(User user) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return Optional.of(
                    session.createQuery("FROM Invite WHERE invited = :user", Invite.class)
                            .setParameter("user", user).list());
        }
    }


    public Optional<Invite> getInviteById(Long id) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.byId(Invite.class).loadOptional(id);
        }
    }

    public boolean removeInvite(Invite invite, Playdate playdate, User user) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();

            user.removeInvite(invite);
            playdate.removeInvite(invite);

            session.update(user);
            session.update(playdate);

            session.remove(invite);
            tx.commit();
            ret = true;
        } catch (Exception e) {
            log.error("error removing invite", e);
            if (tx != null) {
                tx.rollback();
            }
            ret = false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ret;
    }

}
