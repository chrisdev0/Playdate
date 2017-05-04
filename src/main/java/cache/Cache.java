package cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dblayer.HibernateUtil;
import model.DBAPIImage;
import model.Place;
import org.hibernate.Session;
import stockholmapi.APIDataLoader;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.DetailedServiceUnit;

import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Cache {

    private static Cache instance;

    private LoadingCache<String, DBAPIImage> imageCache;

    private APIImageLoader apiImageLoader;


    private Cache() {
        apiImageLoader = new APIImageLoader();

        imageCache = CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterAccess(30, TimeUnit.HOURS)
                .build(apiImageLoader);

    }

    public DBAPIImage getDbImage(String key) throws ExecutionException {
        return imageCache.get(key);
    }



    public void putImageToCache(String key, byte[] image) {
        imageCache.put(key, new DBAPIImage(key, image));
    }

    public static Cache getInstance() {
        if (instance == null) {
            instance = new Cache();
        }
        return instance;
    }

    private class APIImageLoader extends CacheLoader<String, DBAPIImage> {

        @Override
        public DBAPIImage load(String key) throws Exception {
            return APIDataLoader.loadImage(key);
        }
    }

}
