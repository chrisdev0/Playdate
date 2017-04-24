package apilayer.handlers;

import apilayer.StaticFileTemplateHandler;
import dblayer.HibernateUtil;
import model.Place;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetOnePlaceHandler extends StaticFileTemplateHandler {

    private Long placeId;

    public GetOnePlaceHandler(String templateName, long placeId, int onErrorHTTPStatusCode) throws IllegalArgumentException {
        super(templateName,onErrorHTTPStatusCode);
        this.placeId = placeId;
    }

    @Override
    public Optional<Map<String, Object>> createModelMap() {
        try {
            try (Session session = HibernateUtil.getInstance().openSession()) {
                Place place = session.get(Place.class, placeId);
                LoggerFactory.getLogger(GetOnePlaceHandler.class).info("Found place with id " + placeId +" = " + (place != null));
                Hibernate.initialize(place.getComments());
                Map<String, Object> map = new HashMap<>();
                map.put("place", place);
                return Optional.of(map);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(GetOnePlaceHandler.class).info("hibernate error " + Arrays.toString(e.getStackTrace()));
            return Optional.empty();
        }
    }
}
