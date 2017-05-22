package stockholmapi;

import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.GeographicalArea;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import secrets.Secrets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static stockholmapi.helpers.APIUtils.getUrl;

@Slf4j
public class APILoader {

    private HibernateUtil hibernateUtil;
    private List<Object> toAddToDB;

    public APILoader() {
        hibernateUtil = HibernateUtil.getInstance();
        toAddToDB = new ArrayList<>();
    }

    public void doLoadOnStartup(String serviceGuideServiceTypeId) throws Exception {
        final String apiKey = Secrets.STHML_API_KEY;
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
