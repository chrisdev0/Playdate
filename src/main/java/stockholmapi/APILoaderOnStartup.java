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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
        Arrays.asList(sut).forEach(serviceUnitTypes -> places.add(new Place(serviceUnitTypes)));
        Transaction tx = null;
        Session session = null;
        try {
            session = hibernateUtil.openSession();
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
        } finally {
            if (session != null) {
                session.close();
            }
        }
        log.info("finished loading api data after " + (System.currentTimeMillis() - start) + "ms");
    }


}
