package stockholmapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import secrets.Secrets;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.ServiceUnitTypes;

import java.util.ArrayList;
import java.util.List;

import static stockholmapi.helpers.APIUtils.getUrl;
import static stockholmapi.helpers.APIUtils.stupidStockholmAPIJSONToNotStupidJSON;

@Slf4j
public class APILoaderOnStartup {

    private HibernateUtil hibernateUtil;

    public APILoaderOnStartup() {
        hibernateUtil = HibernateUtil.getInstance();
    }

    public void doLoad(String apiKey) throws Exception{
        long start = System.currentTimeMillis();
        log.info("started loading api data");

        String json = stupidStockholmAPIJSONToNotStupidJSON(getUrl(APIUtils.URLS.urlHelper(APIUtils.URLS.BASIC_INFO_PLACEHOLDER, APIUtils.URLS.LEKPLATSER, apiKey)));
        ObjectMapper objectMapper = new ObjectMapper();
        ServiceUnitTypes[] sut = objectMapper.readValue(json, ServiceUnitTypes[].class);
        List<Place> places = new ArrayList<>();
        for (ServiceUnitTypes serviceUnitTypes : sut) {
            Place place = new Place();
            place.setName(serviceUnitTypes.getName());
            place.setSthlmAPIid(serviceUnitTypes.getId());
            place.setGeoX(serviceUnitTypes.getGeographicalPosition().getX());
            place.setGeoY(serviceUnitTypes.getGeographicalPosition().getY());
            place.setTimeCreated(serviceUnitTypes.getTimeCreated());
            place.setTimeUpdated(serviceUnitTypes.getTimeUpdated());
            places.add(place);
        }
        Transaction tx = null;
        try (Session session = hibernateUtil.openSession()) {
            tx = session.beginTransaction();
            for (Place place : places) {
                session.saveOrUpdate(place);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("error committing place data", e);
        }
        log.info("finished loading api data after " + (System.currentTimeMillis() - start) + "ms");
    }


}
