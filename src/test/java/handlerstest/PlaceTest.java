package handlerstest;

import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.PlaceHandler;
import apilayer.handlers.asynchandlers.SearchHandlers;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    @Test
    public void testGetMultiplePlaydatesOfPlace() {
        User user = createUser();
        Place place1 = createPlace();
        Place place2 = createPlace();
        Place place3 = createPlace();

        Playdate playdate1 = createPlaydate(user, place1);
        Playdate playdate2 = createPlaydate(user, place2);
        Playdate playdate3 = createPlaydate(user, place3);


        save(user);
        save(place1);
        save(place2);
        save(place3);

        save(playdate1);
        save(playdate2);
        save(playdate3);


        Request request = initRequestMock(user);
        Response response = initResponseMock();
        injectKeyValue(request, new KeyValue(Paths.QueryParams.MULTI_PLACE_IDS,
                "" + place1.getId() + "," + place2.getId() + "," + place3.getId()));


        String res = (String) SearchHandlers.searchPublicPlaydatesByMultiPlace(request, response);
        log.info("res = " + res);
        assertTrue(responseContainsId(res, playdate1.getId()));
        assertTrue(responseContainsId(res, playdate2.getId()));
        assertTrue(responseContainsId(res, playdate3.getId()));

        remove(playdate1);
        remove(playdate2);
        remove(playdate3);
        remove(place1);
        remove(place2);
        remove(place3);
    }

    @Test
    public void testGetMultiplePlaydatesOfPlace2() {
        User user = createUser();
        Place place1 = createPlace();
        Place place2 = createPlace();
        Place place3 = createPlace();

        Playdate playdate1 = createPlaydate(user, place1);
        Playdate playdate2 = createPlaydate(user, place2);
        Playdate playdate3 = createPlaydate(user, place3);


        save(user);
        save(place1);
        save(place2);
        save(place3);

        save(playdate1);
        save(playdate2);
        save(playdate3);


        Request request = initRequestMock(user);
        Response response = initResponseMock();
        injectKeyValue(request, new KeyValue(Paths.QueryParams.MULTI_PLACE_IDS,
                "" + place1.getId() + "," + place2.getId() + ","));


        String res = (String) SearchHandlers.searchPublicPlaydatesByMultiPlace(request, response);
        log.info("res = " + res);
        assertTrue(responseContainsId(res, playdate1.getId()));
        assertTrue(responseContainsId(res, playdate2.getId()));
        assertFalse(responseContainsId(res, playdate3.getId()));

        remove(playdate1);
        remove(playdate2);
        remove(playdate3);
        remove(place1);
        remove(place2);
        remove(place3);
    }


    @Test
    public void testGetMultiplePlaydatesOfPlaceNoPlaydates() {
        User user = createUser();
        Place place1 = createPlace();
        Place place2 = createPlace();
        Place place3 = createPlace();

        save(user);
        save(place1);
        save(place2);
        save(place3);



        Request request = initRequestMock(user);
        Response response = initResponseMock();
        injectKeyValue(request, new KeyValue(Paths.QueryParams.MULTI_PLACE_IDS,
                "" + place1.getId() + "," + place2.getId() + ","));


        String res = (String) SearchHandlers.searchPublicPlaydatesByMultiPlace(request, response);
        log.info("res = " + res);
        assertEquals("[]",res);

        remove(place1);
        remove(place2);
        remove(place3);
    }



}
