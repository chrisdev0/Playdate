package dblayer;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import model.Playdate;
import model.User;
import org.hibernate.Session;

import java.util.HashSet;
import java.util.List;
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


    public List<Playdate> getPlaydateByOwnerId(Long id) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            try {
                return session.createQuery("FROM Playdate WHERE owner.id = :owner_id", Playdate.class)
                        .setParameter("owner_id", id).list();
            } catch (Exception e) {
                log.error("error ", e);
            }
        }
        return null;
    }

    public Set<Playdate> getPlaydateWhoUserIsAttending(User user) {
        Set<Playdate> playdates = new HashSet<>();
        try (Session session = HibernateUtil.getInstance().openSession()) {
            String hql = "SELECT p FROM Playdate p JOIN p.participants u WHERE u.id = :id";
            playdates.addAll(session.createQuery(hql, Playdate.class).setParameter("id", user.getId()).list());
        }
        playdates.addAll(getPlaydateByOwnerId(user.getId()));
        return playdates;
    }

    public static Object removePlaydateAttendance(Request request, Response response){

        Session session = null;
        Transaction tx = null;
        String playdateId = request.queryParams("playdateId");
        long lId;

        try {
            lId = Long.parseLong(playdateId);
            log.info("Trying to change attendence for playdate with id = " + lId);
        } catch (NullPointerException | NumberFormatException e) {
            log.error("client: " + request.ip() + " sent illegal playdate id = " + playdateId + "error = " + e.getMessage());
            throw halt(400);
        }

        try{
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            User user = request.session().attribute(Constants.USER_SESSION_KEY);
            Playdate playdate = session.get(Playdate.class, lId);

            if(playdate == null){
                log.error("playdate is null");
                throw halt(400);
            }
            if(user == null){
                log.error("user is null");
                throw halt(400);
            }

            if(playdate.getOwner().equals(user)){
                log.error("user is owner of playdate, can't be removed");
                throw halt(400);
            }

            for(User u :playdate.getParticipants()) {
                if (!user.equals(u)) {
                    log.error("user is not a participant");
                    throw halt(400);
                }
                user.removeAttendingPlaydate(playdate);
                playdate.removeParticipant(user);
            }

            session.update(playdate);
            session.update(user);
            tx.commit();

        }catch(Exception e){
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if(session != null){
                session.close();
            }
        }
        return halt(400);
    }
}
