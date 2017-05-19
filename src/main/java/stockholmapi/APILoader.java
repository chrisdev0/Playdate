package stockholmapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.GeographicalArea;
import model.Place;
import org.apache.commons.collections.set.CompositeSet;
import org.hibernate.Session;
import org.hibernate.Transaction;
import secrets.Secrets;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.ServiceUnitTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static stockholmapi.helpers.APIUtils.getUrl;
import static stockholmapi.helpers.APIUtils.stupidStockholmAPIJSONToNotStupidJSON;

@Slf4j
public class APILoader {

    private HibernateUtil hibernateUtil;
    private List<Object> toAddToDB;

    public APILoader() {
        hibernateUtil = HibernateUtil.getInstance();
        toAddToDB = new ArrayList<>();
    }

    public void doLoadOnStartup(String apiKey, String serviceGuideServiceTypeId) throws Exception{
        long start = System.currentTimeMillis();
        log.info("started loading api data");
        List<GeographicalArea> geographicalAreas = Arrays.stream(new GeographicalAreaLoader(apiKey).load())
                .map(GeographicalArea::createFromPOJOGeoArea).collect(Collectors.toList());
        toAddToDB.addAll(geographicalAreas);
        doLoadDuringRun(apiKey, serviceGuideServiceTypeId);
        Transaction tx = null;
        Session session = null;
        try {
            session = hibernateUtil.openSession();
            tx = session.beginTransaction();
            for (Object object : toAddToDB) {
                session.saveOrUpdate(object);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("error committing startup data", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        log.info("finished loading api data after " + (System.currentTimeMillis() - start) + "ms");
    }

    public void doLoadDuringRun(String apiKey, String serviceGuideServiceTypeId) throws Exception{
        new DetailedServiceGuideServiceLoader(apiKey).load(new ServiceGuideServiceLoader(apiKey).load(serviceGuideServiceTypeId), serviceGuideServiceTypeId)
                .forEach(detailedServiceUnit -> toAddToDB.add(Place.constructFromDetailedServiceUnit(detailedServiceUnit)));
    }


}
