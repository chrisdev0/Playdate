package apilayer.handlers;

import apilayer.StaticFileTemplateHandler;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import secrets.Secrets;
import spark.Request;
import stockholmapi.APIDataLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GetOnePlaceHandler extends StaticFileTemplateHandler {

    private Long placeId;

    public GetOnePlaceHandler(String templateName, long placeId, int onErrorHTTPStatusCode) throws IllegalArgumentException {
        super(templateName,onErrorHTTPStatusCode, true);
        this.placeId = placeId;
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            Place place = session.get(Place.class, placeId);
            LoggerFactory.getLogger(GetOnePlaceHandler.class).info("Found place with id " + placeId +" = " + (place != null));
            Hibernate.initialize(place.getComments());
            if (!place.isInitialized()) {
                Transaction transaction = null;
                try {
                    transaction = APIDataLoader.injectPlaceInfo(place, session);
                    transaction.commit();
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    log.error("error updating placeinfo", e);
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("place", place);
            Optional<String> googlemapsAPIKeyOpt = Secrets.getInstance().getValue("googlemapsAPIKey");
            map.put("maps_key", googlemapsAPIKeyOpt.orElse("missing"));
            return Optional.of(map);
        }
    }
}
