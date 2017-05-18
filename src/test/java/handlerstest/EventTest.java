package handlerstest;

import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.EventHandler;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.Playdate;
import model.PlaydateVisibilityType;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import static testutils.ModelCreators.*;
import static org.junit.Assert.*;

@Slf4j
public class EventTest extends MockTestHelpers {



    @Test
    public void testGetByLoc() {
        String N = "59.3493381";
        String E = "17.9950085";
        int gridX = 6583000;
        int gridY = 1624571;

        User user = createUser();
        User pdCreator = createUser();
        Place place = createPlace();
        place.setGeoY(gridY);
        place.setGeoX(gridX);
        Playdate playdate = createPlaydate(pdCreator, place);
        playdate.setPlaydateVisibilityType(PlaydateVisibilityType.PUBLIC);

        save(user);
        save(pdCreator);
        save(place);
        save(playdate);


        Request request = initRequestMock(user);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.LOC_X, N),
                new KeyValue(Paths.QueryParams.LOC_Y, E)
        );

        String res = (String) EventHandler.getPublicPlaydatesCloseToUserFuture(request, response);

        log.info(res);


        remove(playdate);
        remove(place);
        remove(user);
        remove(pdCreator);
    }


}
