package dblayer;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import model.Playdate;
import model.User;
import org.hibernate.Session;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hibernate.Transaction;
import spark.Request;
import spark.Response;

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


    /** Returnerar alla Playdates som en användare (med ett visst id) är ägare till
     *  @param id       id för användaren som är ägare till playdate
     *  @return         en Optional som innehåller alla playdates som användaren med id är ägare till
     * */
    public Optional<List<Playdate>> getPlaydateByOwnerId(Long id) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            try {
                return Optional.of(session.createQuery("FROM Playdate WHERE owner.id = :owner_id", Playdate.class)
                        .setParameter("owner_id", id).list());
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
            playdates.addAll(session.createQuery(hql, Playdate.class).setParameter("id", user.getId()).list());
        }
        getPlaydateByOwnerId(user.getId()).ifPresent(playdates::addAll);
        return playdates;
    }

    /** Sparar en playdate i databasen
     *  @param playdate     playdate som ska sparas i databasen
     *
     *  @return om playdaten kunde sparas i databasen eller inte
     * */
    public boolean updatePlaydate(Playdate playdate) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.update(playdate);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }
        return false;
    }

    /** Sparar en ny playdate i databasen
     *  @param playdate     Playdate som ska sparas i databasen
     *  @param user         ägare för playdate
     * */
    public Optional<Playdate> saveNewPlaydate(Playdate playdate, User user) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            Long id = (Long) session.save(playdate);
            session.save(user);
            tx.commit();
            playdate.setId(id);
            return Optional.of(playdate);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            return Optional.empty();
        }
    }

}
