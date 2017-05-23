package stockholmapi;

import apilayer.Constants;
import cache.Cache;
import com.fasterxml.jackson.databind.ObjectMapper;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.DBAPIImage;
import model.GeographicalArea;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import secrets.Secrets;
import spark.HaltException;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.DetailedServiceUnit;
import stockholmapi.jsontojava.Value2;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static stockholmapi.helpers.APIUtils.*;
import static stockholmapi.helpers.APIUtils.API_HUVUDBILD;

@Slf4j
public class APIDataLoader {


    public static DBAPIImage loadImage(String id) throws Exception {
        DBAPIImage dbapiImage = new DBAPIImage();
        byte[] image = imageUrlToByteArray(APIUtils.URLS.urlHelper(APIUtils.URLS.IMAGE_PLACEHOLDER, id, Secrets.STHML_API_KEY));
        dbapiImage.setApiResourceId(id);
        dbapiImage.setImageAsByte(image);
        return dbapiImage;
    }

    public static void loadGeoAreas() throws Exception {
        List<GeographicalArea> geographicalAreas = Arrays.stream(new GeographicalAreaLoader(Secrets.STHML_API_KEY).load())
                .map(GeographicalArea::createFromPOJOGeoArea).collect(Collectors.toList());
    }

    private static void saveGeoAreas(List<GeographicalArea> geographicalAreas) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            for (GeographicalArea geographicalArea : geographicalAreas) {
                session.saveOrUpdate(geographicalArea);
            }
            tx.commit();
        } catch (Exception e) {
            log.error("error saving geo areas", e);
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}
