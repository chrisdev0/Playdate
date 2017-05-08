package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.Invite;
import model.Playdate;
import model.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hibernate.Transaction;

import static spark.Spark.halt;

@Slf4j
public class PlaydateDAO {


    private static PlaydateDAO instance;

    private PlaydateDAO() {

    }

    public static PlaydateDAO getInstance() {
        if (instance == null) {
            instance = new PlaydateDAO();
        }
        return instance;
    }

    public Optional<Playdate> getPlaydateById(Long id) {
        try(Session session = HibernateUtil.getInstance().openSession()){ return session.byId(Playdate.class).loadOptional(id);}
    }


    /** Returnerar alla Playdates som en användare (med ett visst id) är ägare till
     *  @param id       id för användaren som är ägare till playdate
     *  @return         en Optional som innehåller alla playdates som användaren med id är ägare till
     * */
    public Optional<List<Playdate>> getPlaydateByOwnerId(Long id) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            try {
                List<Playdate> owner_id = session.createQuery("FROM Playdate WHERE owner.id = :owner_id", Playdate.class)
                        .setParameter("owner_id", id).list();
                return Optional.of(owner_id);
            } catch (Exception e) {
                log.error("error ", e);
            }
        }
        return Optional.empty();
    }

    /** Returnerar en lista med alla playdates som en användare attendar
     *  Lägger även till de playdates som användaren står som ägare (eftersom användaren inte kommer vara med
     *  i participants-mängden.
     *  @param user         användaren vars playdates som användaren ska attenda som ska returneras
     *  @return             Listan med playdates
     * */
    public Set<Playdate> getPlaydateWhoUserIsAttending(User user) {
        Set<Playdate> playdates = new HashSet<>();
        try (Session session = HibernateUtil.getInstance().openSession()) {
            String hql = "SELECT p FROM Playdate p JOIN p.participants u WHERE u.id = :id";
            List<Playdate> playdatesList = session.createQuery(hql, Playdate.class).setParameter("id", user.getId()).list();
            playdatesList.forEach(playdate -> Hibernate.initialize(playdate.getParticipants()));
            playdates.addAll(playdatesList);

        }
        getPlaydateByOwnerId(user.getId()).ifPresent(playdates::addAll);
        return playdates;
    }

    public Optional<List<Playdate>> getPlaydatesAttending(User user) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            String hql = "SELECT p FROM Playdate p JOIN p.participants u WHERE u = :user";
            return Optional.of(session.createQuery(hql, Playdate.class).setParameter("user", user).list());
        }
    }


    /** Sparar en playdate i databasen
     *  @param playdate     playdate som ska sparas i databasen
     *
     *  @return om playdaten kunde sparas i databasen eller inte
     * */
    public boolean updatePlaydate(Playdate playdate) {
        boolean ret = false;
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.update(playdate);
            tx.commit();
            ret = true;
        } catch (Exception e) {
            log.error("error updating playdate", e);
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

    /** Sparar en ny playdate i databasen
     *  @param playdate     Playdate som ska sparas i databasen */
    public Optional<Playdate> saveNewPlaydate(Playdate playdate) {
        Optional<Playdate> playdateOptional = Optional.empty();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            Long id = (Long) session.save(playdate);
            session.update(playdate.getOwner());
            tx.commit();
            playdate.setId(id);
            playdateOptional = Optional.of(playdate);
        } catch (Exception e) {
            log.error("error saving playdate", e);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return playdateOptional;
    }

    @SuppressWarnings("Duplicates")
    public boolean deletePlaydate(Playdate playdate) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.remove(playdate);
            tx.commit();
            ret = true;
        } catch (Exception e) {
            log.error("error deleting playdate", e);
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

    public boolean addAttendance(User user, Playdate playdate) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.update(playdate);
            session.update(user);
            playdate.addParticipant(user);
            user.attendPlaydate(playdate);
            tx.commit();
            ret = true;
        } catch (Exception e) {
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

    public boolean removeInvite(Invite invite, Playdate playdate, User user) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.update(user);
            session.update(playdate);

            user.removeInvite(invite);
            playdate.removeInvite(invite);

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
