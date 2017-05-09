package dblayer;

import cache.PlaceSearchCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import model.Place;
import org.hibernate.Session;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SearchDAO {

    private static SearchDAO instance;
    private PlaceSearchCache cache;

    private SearchDAO() {
        cache = new PlaceSearchCache();
    }

    public static SearchDAO getInstance() {
        if (instance == null) {
            instance = new SearchDAO();
        }
        return instance;
    }

    public List<Place> getPlaceByTermThroughCache(String term) throws ExecutionException {
        return cache.getForSearchTerm(term);
    }

    public Optional<List<Place>> placesByStartTerm(String searchTerm, int limit) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return Optional.
                    of(session.createQuery(
                            "FROM Place p WHERE UPPER(name) LIKE :term OR UPPER(geoArea) LIKE :term OR UPPER(streetAddress) LIKE :term"
                            , Place.class)
                            .setParameter("term", searchTerm.toUpperCase() + "%")
                            .setMaxResults(limit)
                            .list());
        }
    }

}
