package dbtest;

import dblayer.SearchDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import org.junit.Test;
import testhelpers.HibernateTests;
import util.ModelCreators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static util.ModelCreators.*;

import static org.junit.Assert.*;

@Slf4j
public class SearchDAOTest extends HibernateTests {


    @Test
    public void testSearchMulti() throws Exception{
        Set<Place> places = new HashSet<>();
        final String search = faker.lorem().characters(20);
        for(int i = 0; i < 10; i++) {
            Place place = createPlace();
            if (i < 3) {
                place.setName(search);
            } else if (i < 6){
                place.setGeoArea(search);
            } else {
                place.setStreetAddress(search);
            }
            places.add(place);
        }
        places.forEach(ModelCreators::save);

        long start = System.currentTimeMillis();
        List<Place> placeByTermThroughCache = SearchDAO.getInstance().getPlaceByTermThroughCache(search);
        log.info("Getting places through cache took " + (System.currentTimeMillis() - start) + "ms");
        assertEquals(5, placeByTermThroughCache.size());




        places.forEach(ModelCreators::remove);
    }

}
