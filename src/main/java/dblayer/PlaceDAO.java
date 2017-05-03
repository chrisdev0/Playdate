package dblayer;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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

    public List<Place> getPlaceByMultiSthlmId(Set<String> ids) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM Place WHERE sthlmAPIid in (:ids)", Place.class)
                    .setParameter("ids", ids)
                    .list();
        }
    }

    public Optional<List<Place>> getPlacesByName(String name) {
        Session session = HibernateUtil.getInstance().openSession();
        return Optional.ofNullable(session.createQuery("FROM Place where Place.name like %:name%", Place.class)
                .setParameter("name", name).list());
    }


    /** Returnerar alla platser som ligger inom området
     *        _______
     *       |       |
     *       |   x   |
     *       |_______|
     *
     * där x är grid koordinaten med
     * @param locX
     * och
     * @param locY
     *
     * och avståndet från x till en vägg är definerad i Constants.GRID_SEARCH_AREA_SIZE
     *
     * */
    public List<Place> getPlaceByLocation(int locX, int locY) {
        Session session = HibernateUtil.getInstance().openSession();
        return session.createQuery("FROM Place WHERE geoX <= :xMax AND geoX >= :xMin AND geoY <= :yMax AND geoY >= :yMin", Place.class)
                .setParameter("xMax", locX + Constants.GRID_SEARCH_AREA_SIZE)
                .setParameter("xMin", locX - Constants.GRID_SEARCH_AREA_SIZE)
                .setParameter("yMax", locY + Constants.GRID_SEARCH_AREA_SIZE)
                .setParameter("yMin", locY - Constants.GRID_SEARCH_AREA_SIZE)
                .list();
    }

    /** ###     ###     ######         ##
     *  ###     ###     ##             ##
     *  ###     ###     ##             ##
     *  ###########     ####           ##
     *  ###     ###     ##             ##
     *  ###     ###     ##       ##   ##
     *  ###     ###     ######     ###
     * */
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
