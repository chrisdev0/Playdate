package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.Playdate;
import model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class PlaydateDao {


    private static PlaydateDao instance;

    private PlaydateDao() {

    }

    public static PlaydateDao getInstance() {
        if (instance == null) {
            instance = new PlaydateDao();
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

}
