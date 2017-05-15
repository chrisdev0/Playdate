package cachetest;

import cache.Cache;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.DBAPIImage;
import model.Place;
import org.junit.Test;

import secrets.Secrets;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import java.util.Optional;

import static org.junit.Assert.*;
import static testutils.ModelCreators.*;

@Slf4j
public class CacheTest extends MockTestHelpers {

    private static final String VALID_PLACE_IMAGE = "ea8bd6dc-8d70-4291-bb28-66c9a97a6ed8";
    private static final String STHML_ID_OF_VALID_IMAGE_PLACE = "5915a3ab-fd9f-4790-ac77-566afe6cbfcd";

    @Test
    public void testCache() throws Exception{
        Request request = initRequestMock(createUser());
        Response response = initResponseMock();
        Cache cache = Cache.getInstance();

        Optional<Place> placeBySthlmId = PlaceDAO.getInstance().getPlaceBySthlmId(STHML_ID_OF_VALID_IMAGE_PLACE);
        if (placeBySthlmId.isPresent()) {
            log.info("Testning with stockholm place");
            DBAPIImage dbImage = cache.getDbImage(VALID_PLACE_IMAGE);
            assertNotNull(dbImage);
            assertTrue(dbImage.getImageAsByte().length > 0);
        } else {
            byte[] image = new byte[10];
            for(int i = 0; i < image.length; i++) {
                image[i] = (byte) (Math.random() * 16);
            }
            DBAPIImage dbapiImage = new DBAPIImage("imagekey", image);
            cache.putImageToCache("imagekey", image);
            DBAPIImage imagekey = cache.getDbImage("imagekey");
            assertNotNull(imagekey);
            assertArrayEquals(image, dbapiImage.getImageAsByte());
        }
    }

    @Test
    public void testPutToCache() throws Exception {
        Secrets.getInstance().loadSecretsFile("secrets.txt");
        Cache cache = Cache.getInstance();
        byte[] image = new byte[10];
        for (int i = 0; i < image.length; i++) {
            image[i] = (byte) (Math.random() * 16);
        }
        DBAPIImage dbapiImage = new DBAPIImage("imagekey", image);
        cache.putImageToCache("imagekey", image);
        DBAPIImage imagekey = cache.getDbImage("imagekey");
        assertNotNull(imagekey);
        assertArrayEquals(image, dbapiImage.getImageAsByte());
    }

}
