package handlerstest;

import apilayer.handlers.PlaceHandler;
import com.google.gson.Gson;
import dblayer.PaginationWrapper;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.User;
import org.junit.Test;

import spark.Request;
import spark.Response;
import util.MockTestHelpers;
import util.ModelCreators;

import java.util.HashSet;
import java.util.Set;

import static util.ModelCreators.*;
import static org.junit.Assert.*;

@Slf4j
public class AsyncTestPlace extends MockTestHelpers {


    @Test
    public void searchPlaceByTerm() {
        User user = createUser();
        Request request = initRequestMock(user);
        Response response = initResponseMock();
        String searchTerm = "---123abc---";
        Set<Place> places = new HashSet<>();

        for(int i = 0; i < 10; i++) {
            Place place = createPlace();
            place.setName(searchTerm);
            places.add(place);
        }
        places.forEach(ModelCreators::save);

        injectSingleKeyValue(request, "name", searchTerm);
        injectSingleKeyValue(request, "offset", "0");

        places.forEach(place -> assertTrue(((String)PlaceHandler.handleGetPlaceByName(request, response)).contains("\"id\":" + place.getId())));
        places.forEach(ModelCreators::remove);
    }

    @Test
    public void


}
