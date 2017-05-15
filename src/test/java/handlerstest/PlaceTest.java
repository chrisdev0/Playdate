package handlerstest;

import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.PlaceHandler;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import java.util.Arrays;

import static org.junit.Assert.*;
import static testutils.ModelCreators.*;

@Slf4j
public class PlaceTest extends MockTestHelpers {


    @Test
    public void testGetPlaceByLoc() {
        User user = createUser();
        Place place = createPlace();
        String N = "59.20738";
        String E = "19.945781";
        int gridX = 6572480;
        int gridY = 1736454;
        place.setGeoY(gridY);
        place.setGeoX(gridX);
        save(user);
        save(place);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.LOC_X, N),
                new KeyValue(Paths.QueryParams.LOC_Y, E)
                );

        String res = (String) PlaceHandler.handleGetPlaceByLoc(request, response);

        assertTrue(responseContainsId(res, place.getId()));

        remove(user);
        remove(place);
    }

    @Test
    public void testGetPlaceByLocNoLoc() {
        User user = createUser();
        Place place = createPlace();
        String N = "59.20738";
        String E = "19.945781";
        int gridX = 6572480;
        int gridY = 1736454;
        place.setGeoY(gridY);
        place.setGeoX(gridX);
        save(user);
        save(place);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.LOC_X, 0L)
                //locY saknas
        );

        String res = (String) PlaceHandler.handleGetPlaceByLoc(request, response);

        assertEquals(res, "error");

        remove(user);
        remove(place);
    }

    @Test
    public void testFindNoPlace() {
        User user = createUser();
        Place place = createPlace();
        String N = "59.20738";
        String E = "19.945781";
        int gridX = 6572480;
        int gridY = 1736454;
        place.setGeoY(gridY);
        place.setGeoX(gridX);
        save(user);
        save(place);

        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.LOC_X, -2000L),
                new KeyValue(Paths.QueryParams.LOC_Y, -2000L)
        );

        String res = (String) PlaceHandler.handleGetPlaceByLoc(request, response);

        assertEquals(res, "[]");

        remove(user);
        remove(place);
    }


}
