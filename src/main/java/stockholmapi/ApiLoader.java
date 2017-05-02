package stockholmapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import dblayer.HibernateUtil;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import secrets.Secrets;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.DetailedServiceUnit;
import stockholmapi.jsontojava.ServiceUnitTypes;
import stockholmapi.jsontojava.Value2;
import stockholmapi.temporaryobjects.PlaceHolder;
import stockholmapi.temporaryobjects.PlaceHolderBuilder;


import javax.sql.rowset.serial.SerialBlob;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static stockholmapi.helpers.APIUtils.*;

public class ApiLoader {

    public static void main(String[] args) throws Exception{
        final String API_KEY = Secrets.getInstance().loadSecretsFile("secrets.txt").getValue("stockholmAPIKEY").get();
        HibernateUtil hibernateUtil = HibernateUtil.getInstance();
        long startTime = System.currentTimeMillis();

        String json = stupidStockholmAPIJSONToNotStupidJSON(getUrl(URLS.urlHelper(URLS.BASIC_INFO_PLACEHOLDER, URLS.LEKPLATSER, API_KEY)));

        ObjectMapper objectMapper = new ObjectMapper();

        ServiceUnitTypes[] sut = objectMapper.readValue(json, ServiceUnitTypes[].class);

        List<PlaceHolder> placeHolders = new ArrayList<>();

        for (ServiceUnitTypes serviceUnitTypes : sut) {
            placeHolders.add(buildPlaceHolder(serviceUnitTypes));
        }

        int imageFail = 0;
        for (PlaceHolder placeHolder : placeHolders) {
            URL url = URLS.urlHelper(URLS.DETAILED_INFO_PLACEHOLDER, placeHolder.getId(), API_KEY);
            System.out.println(url.toString());
            String placeJson = null;
            try {
                placeJson = stupidStockholmAPIJSONToNotStupidJSON(getUrl(url));
            } catch (Exception e) {
                System.out.println("Failed to load url " + url.toString() + " code " + e.getMessage());
                continue;
            }
            DetailedServiceUnit detailedServiceUnit = objectMapper.readValue(placeJson, DetailedServiceUnit.class);
            detailedServiceUnit.createMapOfAttributes();
            placeHolder.setCategory(detailedServiceUnit.getServiceUnitTypes().get(0).getPluralName());
            placeHolder.setCityAddress((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_POST_ADDRESS));
            placeHolder.setGeoArea(detailedServiceUnit.getGeographicalAreas().get(0).getFriendlyId());
            placeHolder.setDescription((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_SHORT_DESC));
            placeHolder.setZip((String) detailedServiceUnit.getAttributesIdToValue().get(API_ZIP));
            placeHolder.setStreetAddress((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_STREET_ADDRESS));
            Object object = detailedServiceUnit.getAttributesIdToValue().get(API_HUVUDBILD);
            if (object != null && object instanceof Value2) {
                Value2 value2 = (Value2) object;
                byte[] image;
                try {
                    image = imageUrlToByteArray(URLS.urlHelper(URLS.IMAGE_PLACEHOLDER, value2.getId(), API_KEY));
                } catch (Exception e) {
                    System.out.println("Failed to load image because " + e.getMessage());
                    imageFail++;
                    continue;
                }
                System.out.println("image byte array length =" + image.length);
                placeHolder.setImage(new SerialBlob(image));
            }
        }
        System.out.println("Starting db save after " + ((System.currentTimeMillis() - startTime) / 1000) + " sekunder.\n Hämtade " + placeHolders.size() + " Place av totalt " + sut.length + "möjliga");
        System.out.println("Failed to load " + imageFail + " images");

        Transaction tx = null;
        try (Session session = hibernateUtil.openSession()) {
            tx = session.beginTransaction();
            for (PlaceHolder placeHolder : placeHolders) {
                session.save(placeHolder);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        System.out.println("loading ended after " + ((System.currentTimeMillis() - startTime) / 1000) + " sekunder.\n Hämtade " + placeHolders.size() + " Place");
    }

    public static PlaceHolder buildPlaceHolder(ServiceUnitTypes sut) {
        PlaceHolderBuilder builder = new PlaceHolderBuilder();
        return builder.setId(sut.getId())
                .setName(sut.getName())
                .setX(sut.getGeographicalPosition().getX())
                .setY(sut.getGeographicalPosition().getY())
                .createPlaceHolder();
    }



}
