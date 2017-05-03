package stockholmapi;

import apilayer.Constants;
import cache.Cache;
import com.fasterxml.jackson.databind.ObjectMapper;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.DBAPIImage;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import secrets.Secrets;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.DetailedServiceUnit;
import stockholmapi.jsontojava.Value2;

import java.net.URL;
import java.util.Optional;

import static stockholmapi.helpers.APIUtils.*;
import static stockholmapi.helpers.APIUtils.API_HUVUDBILD;

@Slf4j
public class APIDataLoader {

    private static String API_KEY = null;

    public static DBAPIImage loadImage(String id) throws Exception {
        DBAPIImage dbapiImage = new DBAPIImage();
        byte[] image = imageUrlToByteArray(APIUtils.URLS.urlHelper(APIUtils.URLS.IMAGE_PLACEHOLDER, id, getApiKey()));
        dbapiImage.setApiResourceId(id);
        dbapiImage.setImageAsByte(image);
        return dbapiImage;
    }

    private static String getApiKey() {
        if (API_KEY == null) {
            Optional<String> stockholmAPIKEYOPT = Secrets.getInstance().getValue("stockholmAPIKEY");
            if (stockholmAPIKEYOPT.isPresent()) {
                API_KEY = stockholmAPIKEYOPT.get();
            } else {
                throw new RuntimeException("Couldn't get stockholm api key from secrets file");
            }
        }
        return API_KEY;
    }

    public static Transaction injectPlaceInfo(Place place,Session session) throws Exception {
        String apiId = place.getSthlmAPIid();
        ObjectMapper objectMapper = new ObjectMapper();
        URL url = APIUtils.URLS.urlHelper(APIUtils.URLS.DETAILED_INFO_PLACEHOLDER, apiId, getApiKey());
        String placeJson;
        try {
            placeJson = stupidStockholmAPIJSONToNotStupidJSON(getUrl(url));
        } catch (Exception e) {
            log.error("Failed loading data for place = " + place.getName(), e);
            throw new Exception("failed to load data for place", e);
        }
        DetailedServiceUnit detailedServiceUnit = objectMapper.readValue(placeJson, DetailedServiceUnit.class);
        detailedServiceUnit.createMapOfAttributes();
        place.injectInfo(detailedServiceUnit);
        Object object = detailedServiceUnit.getAttributesIdToValue().get(API_HUVUDBILD);
        if (object != null && object instanceof Value2) {
            Value2 value2 = (Value2) object;
            byte[] image;
            try {
                image = imageUrlToByteArray(URLS.urlHelper(URLS.IMAGE_PLACEHOLDER, value2.getId(), API_KEY));
                Cache.getInstance().putImageToCache(value2.getId(), image);
                place.setImageId(value2.getId());
            } catch (Exception e) {
                log.error("Failed to load image because ", e);
            }
        } else {
            place.setImageId(Constants.MAGIC_MISSING_IMAGE);
        }
        Transaction tx = session.beginTransaction();
        session.update(place);
        return tx;
    }
}
