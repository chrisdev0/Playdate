package dblayer;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.filters.TimeFilterable;
import utils.filters.TimeFilterable.TimeFilter;

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


    /**
     * Returnerar alla Playdates som en användare (med ett visst id) är ägare till
     *
     * @param id id för användaren som är ägare till playdate
     * @param timeFilter
     * @return en Optional som innehåller alla playdates som användaren med id är ägare till
     */
    public Optional<List<Playdate>> getPlaydateByOwnerId(Long id, TimeFilter timeFilter) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            try {
                List<Playdate> owner_id = injectTimeToQuery(session, "FROM Playdate p WHERE p.owner.id = :owner_id", timeFilter)
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
     *  @param timeFilter
     * @return             Listan med playdates
     * */
    public Set<Playdate> getAllPlaydateWhoUserIsAttendingAlsoOwner(User user, TimeFilter timeFilter) {
        Set<Playdate> playdates = new HashSet<>();
        try (Session session = HibernateUtil.getInstance().openSession()) {
            String hql = "SELECT p FROM Playdate p JOIN p.participants u WHERE u.id = :id";
            List<Playdate> playdatesList = injectTimeToQuery(session,hql,timeFilter).setParameter("id", user.getId()).list();
            playdatesList.forEach(playdate -> Hibernate.initialize(playdate.getParticipants()));
            playdates.addAll(playdatesList);
        }
        getPlaydateByOwnerId(user.getId(), timeFilter).ifPresent(playdates::addAll);
        return playdates;
    }

    public List<Playdate> getPlaydatesOfMultiplePlace(List<Long> ids, TimeFilter timeFilter) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return injectTimeToQuery(session, "FROM Playdate WHERE  playdateVisibilityType = :vis AND place.id IN (:ids)", timeFilter)
                    .setParameter("ids", ids).setParameter("vis", PlaydateVisibilityType.PUBLIC).list();
        }
    }

    public Optional<List<Playdate>> getPlaydatesAttending(User user, TimeFilter timeFilter) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            //language=HQL
            String hql = "SELECT p FROM Playdate p JOIN p.participants u WHERE u = :user";
            return Optional.of(injectTimeToQuery(session, hql, timeFilter)
                    .setParameter("user", user).list());
        }
    }


    /** Sparar en playdate i databasen
     *  @param playdate     playdate som ska sparas i databasen
     *
     *  @return om playdaten kunde sparas i databasen eller inte
     * */
    @SuppressWarnings("Duplicates")
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
            session.save(playdate);
            session.update(playdate.getOwner());
            tx.commit();
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
        Optional<Invite> inviteOfUserAndPlaydate = InviteDAO.getInstance().getInviteOfUserAndPlaydate(user, playdate);
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            if (inviteOfUserAndPlaydate.isPresent()) {
                session.remove(inviteOfUserAndPlaydate.get());
            }
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

    public void refreshPlaydate(Playdate playdate) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            session.refresh(playdate);
        }
    }

    public boolean acceptAndAddAttendance(Invite invite) {
        Session session = null;
        Transaction tx = null;
        User user = invite.getInvited();
        Playdate playdate = invite.getPlaydate();
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();

            playdate.addParticipant(user);
            user.attendPlaydate(playdate);
            user.getInvitesToPlaydates().remove(invite);//tror den ska vara här

            session.update(playdate);
            session.update(user);
            session.remove(invite);

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

    public boolean removeAttendance(Playdate playdate, User user) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            if (playdate.removeParticipant(user)) {
                if (user.removeAttendingPlaydate(playdate)) {
                    session.update(playdate);
                    session.update(user);
                    ret = true;
                }
            } else {
                ret = false;
            }
            tx.commit();
        } catch (Exception e) {
            log.error("error removing attendance", e);
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

    public Optional<List<Playdate>> getPublicPlaydatesByLoc(int locX, int locY, TimeFilter timeFilter) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return Optional.of(
                    injectTimeToQuery(session,"FROM Playdate p WHERE playdateVisibilityType = :vis AND p.place.geoX <= :xMax AND p.place.geoX >= :xMin AND " +
                            "p.place.geoY <= :yMax AND p.place.geoY >= :yMin",timeFilter)
                    .setParameter("xMax", locX + Constants.GRID_SEARCH_AREA_SIZE)
                    .setParameter("xMin", locX - Constants.GRID_SEARCH_AREA_SIZE)
                    .setParameter("yMax", locY + Constants.GRID_SEARCH_AREA_SIZE)
                    .setParameter("yMin", locY - Constants.GRID_SEARCH_AREA_SIZE)
                    .setParameter("vis", PlaydateVisibilityType.PUBLIC)
                    .list());
        }
    }

    public List<Playdate> getPlaydateAtPlace(Place place, TimeFilter timeFilter) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return injectTimeToQuery(session,"FROM Playdate p WHERE place = :place AND playdateVisibilityType = :vis",timeFilter)
                    .setParameter("place", place)
                    .setParameter("vis", PlaydateVisibilityType.PUBLIC).list();
        }
    }

    @SuppressWarnings("Duplicates")
    public Optional<Set<Comment>> savePlaydateComment(Comment comment, Playdate playdate) {
        Set<Comment> ret = null;
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            Hibernate.initialize(playdate.getComments());
            playdate.addComment(comment);
            session.save(comment);
            session.update(playdate);
            tx.commit();
            ret = playdate.getComments();
        } catch (Exception e) {
            log.error("error save comment", e);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return Optional.ofNullable(ret);
    }

    public List<Playdate> getPlaydatesWhoUserIsNotAttendingButCanAttendThroughFriend(User user, TimeFilter timeFilter) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            session.refresh(user);
            String hql = "SELECT distinct p FROM Playdate p, User, Friendship f WHERE f.requester = :user AND f.friend = p.owner " +
                    "AND (p.playdateVisibilityType = :public OR p.playdateVisibilityType = :friendsonly) AND (:userid NOT IN (SELECT par.id FROM p.participants par WHERE par.id = :userid))";
            return injectTimeToQuery(session, hql, timeFilter)
                    .setParameter("user", user)
                    .setParameter("userid", user.getId())
                    .setParameter("public", PlaydateVisibilityType.PUBLIC)
                    .setParameter("friendsonly", PlaydateVisibilityType.FRIENDS_ONLY)
                    .list();
        }
    }

    private void injectStartParameter(Query<Playdate> query, TimeFilter timeFilter) {
        switch (timeFilter) {
            case FUTURE:
            case NEAR_FUTURE:
            case RECENT_HAPPENED:
                query.setParameter("starttime", TimeFilterable.getStartTime(timeFilter));
                query.setParameter("starttime", TimeFilterable.getStartTime(timeFilter));
        }
    }


    private void injectEndParameter(Query<Playdate> query, TimeFilter timeFilter) {
        switch (timeFilter) {
            case NEAR_FUTURE:
            case RECENT_HAPPENED:
            case HISTORY:
                query.setParameter("endtime", TimeFilterable.getEndTime(timeFilter));
        }
    }



    private Query<Playdate> injectTimeToQuery(Session session, String hql, TimeFilter timeFilter) {
        Query<Playdate> query = session.createQuery(injectTimeIntoHQL(hql,timeFilter), Playdate.class);
        injectStartParameter(query,timeFilter);
        injectEndParameter(query, timeFilter);
        return query;
    }

    private String injectTimeIntoHQL(String hql, TimeFilter timeFilter) {
        switch (timeFilter) {
            case NEAR_FUTURE:
                return hql + " AND p.startTime >= :starttime AND p.startTime <= :endtime";
            case FUTURE:
                return hql + " AND p.startTime >= :starttime";
            case RECENT_HAPPENED:
            case HISTORY:
                return hql + " AND p.startTime <= :endtime";
            default:
                return hql;
        }
    }

    public List<User> getPotentialFriendsToInvite(Playdate playdate) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            String hql = "SELECT DISTINCT fr.friend FROM Friendship fr WHERE fr.requester = :user AND " +
                    " fr.friend NOT IN " +
                    "(SELECT participant FROM Playdate p JOIN p.participants participant WHERE p = :playdate) AND " +
                    "fr.friend NOT IN " +
                    "(SELECT i.invited FROM Invite i WHERE i.playdate = :playdate)";
            return session.createQuery(hql, User.class)
                    .setParameter("user", playdate.getOwner())
                    .setParameter("playdate",playdate)
                    .list();
        }
    }

}
