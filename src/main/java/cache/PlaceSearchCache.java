package cache;

import apilayer.Constants;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dblayer.SearchDAO;
import model.Place;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PlaceSearchCache {

    private LoadingCache<String, List<Place>> searchLoader;

    public PlaceSearchCache() {
        searchLoader = CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterAccess(60, TimeUnit.SECONDS)
                .build(new PlaceLoader());
    }


    public List<Place> getForSearchTerm(String searchTerm) throws ExecutionException {
        return searchLoader.get(searchTerm);
    }

    private class PlaceLoader extends CacheLoader<String, List<Place>> {

        @Override
        public List<Place> load(String s) throws Exception {
            Optional<List<Place>> places = SearchDAO.getInstance().placesByStartTerm(s, Constants.QUICK_PLACE_SEARCH_LIMIT);
            return places.orElse(Collections.emptyList());
        }
    }

}
