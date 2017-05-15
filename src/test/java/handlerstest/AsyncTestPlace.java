package handlerstest;

import apilayer.handlers.asynchandlers.PlaceHandler;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import org.junit.Test;

import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;
import testutils.ModelCreators;

import java.util.HashSet;
import java.util.Set;

import static testutils.ModelCreators.*;
import static org.junit.Assert.*;

@Slf4j
public class AsyncTestPlace extends MockTestHelpers {


    @Test
    public void searchPlaceByName() {
        Request request = initRequestMock(createUser());
        Response response = initResponseMock();
        String searchTerm = "---123abc---";
        Set<Place> places = new HashSet<>();

        for(int i = 0; i < 10; i++) {
            Place place = createPlace();
            place.setName(searchTerm);
            places.add(place);
        }
        places.forEach(ModelCreators::save);

        injectKeyValue(request, "name", searchTerm);
        injectKeyValue(request, "offset", "0");

        String json = (String) PlaceHandler.handleGetPlaceByName(request, response);
        places.forEach(place -> {
            assertTrue(json.contains("\"id\":" + place.getId()));
        });
        places.forEach(ModelCreators::remove);
    }

    @Test
    public void searchByGeoArea() {
        Request request = initRequestMock(createUser());
        Response response = initResponseMock();
        String searchTerm = "GEEOOAARREEAA";
        Set<Place> places = new HashSet<>();
        for(int i = 0; i < 10; i++) {
            Place place = createPlace();
            place.setGeoArea(searchTerm);
            places.add(place);
        }
        places.forEach(ModelCreators::save);

        injectKeyValue(request, new KeyValue("name", searchTerm), new KeyValue("offset", "0"));
        String json = (String) PlaceHandler.handleGetPlaceByGeoArea(request, response);

        places.forEach(place -> assertTrue(json.contains("\"id\":" + place.getId())));

        places.forEach(ModelCreators::remove);
    }


    @Test
    public void searchByGeoAreaMoreThan10() {
        Request request = initRequestMock(createUser());
        Response response = initResponseMock();
        String searchTerm = "GEEOOAARREEAA";
        Set<Place> places = new HashSet<>();
        for(int i = 0; i < 25; i++) {
            Place place = createPlace();
            place.setGeoArea(searchTerm);
            places.add(place);
        }
        places.forEach(ModelCreators::save);

        injectKeyValue(request, new KeyValue("name", searchTerm), new KeyValue("offset", "0"));
        String json = (String) PlaceHandler.handleGetPlaceByGeoArea(request, response);

        injectKeyValue(request, new KeyValue("name", searchTerm), new KeyValue("offset", "10"));
        String json2 = (String) PlaceHandler.handleGetPlaceByGeoArea(request, response);

        injectKeyValue(request, new KeyValue("name", searchTerm), new KeyValue("offset", "20"));
        String json3 = (String) PlaceHandler.handleGetPlaceByGeoArea(request, response);

        places.forEach(place -> assertTrue(
                json.contains("\"id\":" + place.getId())
                        ||
                        json2.contains("\"id\":" + place.getId())
                        ||
                        json3.contains("\"id\":" + place.getId())
        ));
        places.forEach(ModelCreators::remove);
    }



}
