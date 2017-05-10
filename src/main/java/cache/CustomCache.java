package cache;

import java.util.concurrent.ConcurrentHashMap;

public class CustomCache {

    public static CustomCache instance;

    private ConcurrentHashMap<String, Object> cache;


    private CustomCache() {
        cache = new ConcurrentHashMap<>();
    }

    public static CustomCache getInstance() {
        if (instance == null) {
            instance = new CustomCache();
        }
        return instance;
    }


    public Object getResultOfUrl(String key) {
        if (cache.contains(key)) {
            return cache.get(key);
        }
        return null;
    }


}
