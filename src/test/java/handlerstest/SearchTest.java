package handlerstest;

import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.SearchHandlers;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;
import testutils.ModelCreators;
import utils.CoordinateHandlerUtil;
import utilstest.CoordinatHandlerUtilTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static testutils.ModelCreators.*;
import static org.junit.Assert.*;

@Slf4j
public class SearchTest extends MockTestHelpers {

    private List<Object> stuffToDelete;




    @Test
    public void testSearchPlaydateByLoc() {
        User user = createUser();
        Place place = createPlace();
        place.setGeoX(6595334);
        place.setGeoY(1593493);
        Playdate playdate = createPlaydate(user, place);
        Place place1 = createPlace();
        place1.setGeoX(6595334);
        place1.setGeoY(1593493);
        Playdate playdate1 = createPlaydate(user, place1);
        save(user);
        save(place);
        save(place1);
        save(playdate);
        save(playdate1);

        Request request = initRequestMock(user);
        Response response = initResponseMock();
        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.LOC_X, "59.468029"),
                new KeyValue(Paths.QueryParams.LOC_Y, "17.454342")
        );


        String res = (String)SearchHandlers.searchPublicPlaydatesByLoc(request, response);
        assertTrue(res.contains("\"id\":" + playdate.getId() + ","));
        assertTrue(res.contains("\"id\":" + playdate1.getId() + ","));



        remove(playdate);
        remove(playdate1);
        remove(place);
        remove(place1);
        remove(user);
    }


}
