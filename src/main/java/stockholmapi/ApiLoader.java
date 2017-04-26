package stockholmapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import secrets.Secrets;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.DetailedServiceUnit;
import stockholmapi.jsontojava.ServiceUnitTypes;
import stockholmapi.jsontojava.Value2;
import stockholmapi.temporaryobjects.PlaceHolder;
import stockholmapi.temporaryobjects.PlaceHolderBuilder;


import javax.sql.rowset.serial.SerialBlob;
import java.util.ArrayList;
import java.util.List;

import static stockholmapi.helpers.APIUtils.*;

public class ApiLoader {

    public static void main(String[] args) throws Exception{
        final String API_KEY = Secrets.getInstance().loadSecretsFile("secrets.txt").getValue("stockholmAPIKEY").get();

        String jsonURL = "http://api.stockholm.se/ServiceGuideService/DetailedServiceUnits/134597ad-0ed7-47fc-b324-31686537d1b6/json?apikey=a42963ca81a64e55869481b281ad36c0";

        String json = stupidStockholmAPIJSONToNotStupidJSON(getUrl(URLS.urlHelper(URLS.BASIC_INFO_PLACEHOLDER, URLS.LEKPLATSER, API_KEY)));

        ObjectMapper objectMapper = new ObjectMapper();

        ServiceUnitTypes[] sut = objectMapper.readValue(json, ServiceUnitTypes[].class);

        List<PlaceHolder> placeHolders = new ArrayList<>();

        for (ServiceUnitTypes serviceUnitTypes : sut) {
            placeHolders.add(buildPlaceHolder(serviceUnitTypes));
        }

        String placeJson = stupidStockholmAPIJSONToNotStupidJSON(getUrl(URLS.urlHelper(URLS.DETAILED_INFO_PLACEHOLDER, placeHolders.get(0).getId(), API_KEY)));
        DetailedServiceUnit detailedServiceUnit = objectMapper.readValue(placeJson, DetailedServiceUnit.class);
        System.out.println(detailedServiceUnit);
        detailedServiceUnit.createMapOfAttributes();
        PlaceHolder placeHolder = placeHolders.get(0);
        placeHolder.setCategory(detailedServiceUnit.getServiceUnitTypes().get(0).getPluralName());
        placeHolder.setCityAddress((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_POST_ADDRESS));
        placeHolder.setGeoArea(detailedServiceUnit.getGeographicalAreas().get(0).getFriendlyId());
        placeHolder.setDescription((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_SHORT_DESC));
        placeHolder.setZip((String) detailedServiceUnit.getAttributesIdToValue().get(API_ZIP));
        placeHolder.setStreetAddress((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_STREET_ADDRESS));
        Object object = detailedServiceUnit.getAttributesIdToValue().get(API_HUVUDBILD);
        if (object != null && object instanceof Value2) {
            Value2 value2 = (Value2) object;
            byte[] image = imageUrlToByteArray(URLS.urlHelper(URLS.IMAGE_PLACEHOLDER, value2.getId(), API_KEY));
            System.out.println("image byte array length =" + image.length);
            placeHolder.setImage(new SerialBlob(image));
        }

        System.out.println(placeHolder);
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
