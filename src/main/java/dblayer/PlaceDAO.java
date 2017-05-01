package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class PlaceDAO {

    private static PlaceDAO instance;

    private PlaceDAO() {

    }

    public static PlaceDAO getInstance() {
        if (instance == null) {
            instance = new PlaceDAO();
        }
        return instance;
    }


    public Optional<Place> getPlaceBySthlmId(String sthlmId) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM Place WHERE sthlmAPIid = :sthlmAPIid", Place.class)
                    .setParameter("sthlmAPIid", sthlmId)
                    .uniqueResultOptional();
        }
    }

    public List<Place> getPlaceByMultisthlmId(Set<String> ids) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM Place WHERE sthlmAPIid in (:ids)", Place.class)
                    .setParameter("ids", ids)
                    .list();
        }
    }

    public Optional<Place> getPlaceById(Long placeId) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.byId(Place.class).loadOptional(placeId);
        }
    }

    public boolean deletePlaceById(Long placeId) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            Optional<Place> placeOptional = session.byId(Place.class).loadOptional(placeId);
            if (placeOptional.isPresent()) {
                session.remove(placeOptional.get());
                ret = true;
            }
            tx.commit();
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

    public boolean storeOrUpdatePlace(Place place) {
        Session session = null;
        Transaction tx = null;
        boolean ret = false;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.saveOrUpdate(place);
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
