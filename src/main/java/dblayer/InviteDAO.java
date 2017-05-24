package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.Invite;
import model.Playdate;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

@Slf4j
public class InviteDAO {

    private static InviteDAO instance;

    private InviteDAO(){}

    public static InviteDAO getInstance() {
        if (instance == null) {
            instance = new InviteDAO();
        }
        return instance;
    }

    public boolean addInviteToUserAndPlaydate(Invite invite) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            log.info("hashcode of invite before save= " + invite.hashCode());
            session.save(invite);
            log.info("hashcode of invite after save = " + invite.hashCode());
            invite.getPlaydate().addInvite(invite);
            invite.getInvited().addInvite(invite);
            session.update(invite.getPlaydate());
            session.update(invite.getInvited());
            log.info("looping through user invites");
            invite.getInvited().getInvitesToPlaydates().forEach(invite1 -> log.info(invite1.toString()));
            invite.getInvited().getInvitesToPlaydates().forEach(invite1 -> log.info("" + invite1.hashCode()));

            log.info("looping through playdate invites");
            invite.getPlaydate().getInvites().forEach(invite1 -> log.info(invite1.toString()));
            invite.getPlaydate().getInvites().forEach(invite1 -> log.info("" + invite1.hashCode()));

            log.info("invite = " + invite.toString());
            log.info("invite hashcode = " + invite.hashCode());

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

        log.info("looping through user invites");
        invite.getInvited().getInvitesToPlaydates().forEach(invite1 -> log.info(invite1.toString()));
        invite.getInvited().getInvitesToPlaydates().forEach(invite1 -> log.info("" + invite1.hashCode()));

        log.info("looping through playdate invites");
        invite.getPlaydate().getInvites().forEach(invite1 -> log.info(invite1.toString()));
        invite.getPlaydate().getInvites().forEach(invite1 -> log.info("" + invite1.hashCode()));

        log.info("invite = " + invite.toString());
        log.info("invite hashcode = " + invite.hashCode());



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

    public Optional<Invite> getInviteOfUserAndPlaydate(User user, Playdate playdate) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            String hql = "FROM Invite WHERE invited = :user AND playdate = :playdate";
            return session.createQuery(hql, Invite.class)
                    .setParameter("user", user)
                    .setParameter("playdate", playdate).uniqueResultOptional();
        }
    }

    public boolean removeInvite(Invite invite) {
        return removeInvite(invite, invite.getPlaydate(), invite.getInvited());
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

            session.update(playdate);
            session.remove(invite);
            session.update(user);

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
