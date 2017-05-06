package dbtest;

import dblayer.HibernateUtil;
import dblayer.PaginationWrapper;
import dblayer.PlaceDAO;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.Comment;
import model.Place;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;
import testhelpers.HibernateTests;
import util.ModelCreators;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Slf4j
public class PlaceDAOTest extends HibernateTests {

    @BeforeClass
    public static void cleanupFromFailedTests() {
        HibernateUtil.getInstance();
        int sizeOfResult = 0;
        do {
            Optional<PaginationWrapper<Place>> placesByGeoArea = PlaceDAO.getInstance().getPlacesByGeoArea(ModelCreators.createPlace().getGeoArea(), 0, 100);
            sizeOfResult = placesByGeoArea.get().getCollection().size();
            placesByGeoArea.get().stream().forEach(place -> PlaceDAO.getInstance().deletePlaceById(place.getId()));
        } while (sizeOfResult < 0);
    }

    @Test
    public void testSavePlace() {
        Place place = ModelCreators.createPlace();
        boolean b = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b);
        assertNotNull(place.getId());
        boolean b1 = PlaceDAO.getInstance().deletePlaceById(place.getId());
        assertTrue(b1);
    }

    @Test
    public void testSaveComment() {
        Place place = ModelCreators.createPlace();
        Comment comment = new Comment();
        comment.setComment("Comment");
        User user = ModelCreators.createUser();
        comment.setCommenter(user);

        boolean b = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b);
        assertNotNull(place.getId());

        boolean b2 = UserDAO.getInstance().saveUserOnLogin(user);
        assertTrue(b2);
        assertNotNull(user.getId());

        place.addComment(comment);
        PlaceDAO.getInstance().saveComment(comment, place);


        boolean b1 = PlaceDAO.getInstance().deletePlaceById(place.getId());
        assertTrue(b1);

        boolean b3 = UserDAO.getInstance().deleteUser(user);
        assertTrue(b3);
    }

    @Test
    public void getCommentsOfPlace() {
        Place place = ModelCreators.createPlace();
        Comment comment = new Comment();
        comment.setComment("Comment");
        User user = ModelCreators.createUser();
        comment.setCommenter(user);

        boolean b = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b);
        assertNotNull(place.getId());

        boolean b2 = UserDAO.getInstance().saveUserOnLogin(user);
        assertTrue(b2);
        assertNotNull(user.getId());

        place.addComment(comment);
        PlaceDAO.getInstance().saveComment(comment, place);

        Optional<Set<Comment>> commentsForPlace = PlaceDAO.getInstance().getCommentsForPlace(place);

        assertTrue(commentsForPlace.isPresent());
        assertNotNull(commentsForPlace.get());
        for (Comment comment1 : commentsForPlace.get()) {
            assertEquals(comment.getId(),comment1.getId());
        }


        boolean b1 = PlaceDAO.getInstance().deletePlaceById(place.getId());
        assertTrue(b1);

        boolean b3 = UserDAO.getInstance().deleteUser(user);
        assertTrue(b3);
    }

    @Test
    public void saveIllegalComment() {
        Place place = ModelCreators.createPlace();
        Comment comment = new Comment();
        comment.setComment(null);
        User user = ModelCreators.createUser();
        comment.setCommenter(user);

        boolean b = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b);
        assertNotNull(place.getId());

        boolean b2 = UserDAO.getInstance().saveUserOnLogin(user);
        assertTrue(b2);
        assertNotNull(user.getId());

        place.addComment(comment);
        Optional<Set<Comment>> comments = PlaceDAO.getInstance().saveComment(comment, place);

        assertFalse(comments.isPresent());


        boolean b1 = PlaceDAO.getInstance().deletePlaceById(place.getId());
        assertTrue(b1);

        boolean b3 = UserDAO.getInstance().deleteUser(user);
        assertTrue(b3);
    }

    @Test
    public void testIllegalPlace() {
        Place place = ModelCreators.createPlace();
        place.setName(null);
        boolean b = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertFalse(b);
    }

    @Test
    public void getPlaceTest() {
        Place place = ModelCreators.createPlace();
        boolean b = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b);
        assertNotNull(place.getId());

        Optional<Place> placeById = PlaceDAO.getInstance().getPlaceById(place.getId());
        assertTrue(placeById.isPresent());
        assertEquals(placeById.get().getId(), place.getId());

        boolean b1 = PlaceDAO.getInstance().deletePlaceById(place.getId());
        assertTrue(b1);
    }

    @Test
    public void testGetPlaceThatDoesntExist() {
        Optional<Place> placeById = PlaceDAO.getInstance().getPlaceById(-1L);
        assertFalse(placeById.isPresent());
    }

    @Test
    public void testGetPlaceThatDoesntExist3() {
        Optional<List<Place>> placeByMultiSthlmId1 = PlaceDAO.getInstance().getPlaceByMultiSthlmId(null);
        assertTrue(placeByMultiSthlmId1.isPresent());
        assertEquals(0,placeByMultiSthlmId1.get().size());
    }

    @Test
    public void testGetPlaceThatDoesntExist2() {
        Optional<Place> placeById = PlaceDAO.getInstance().getPlaceBySthlmId("-");
        assertFalse(placeById.isPresent());
    }

    @Test
    public void testGetMultiplePlace() {
        Set<Place> places = new HashSet<>();
        places.add(ModelCreators.createPlace());
        places.add(ModelCreators.createPlace());
        places.add(ModelCreators.createPlace());
        places.add(ModelCreators.createPlace());
        places.forEach(PlaceDAO.getInstance()::storeOrUpdatePlace);
        places.forEach(p -> assertNotNull(p.getId()));
        Set<String> ids = places.stream().map(Place::getSthlmAPIid).collect(Collectors.toSet());
        Optional<List<Place>> placeByMultiSthlmId = PlaceDAO.getInstance().getPlaceByMultiSthlmId(ids);
        assertTrue(placeByMultiSthlmId.isPresent());

        assertEquals(places.size(), placeByMultiSthlmId.get().size());

        for (Place p : places) {
            boolean b = PlaceDAO.getInstance().deletePlaceById(p.getId());
            assertTrue(b);
        }
    }

    @Test
    public void testGetPlaceByGeoArea() {
        Place place = ModelCreators.createPlace();
        boolean b = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b);
        assertNotNull(place.getId());

        Optional<PaginationWrapper<Place>> placesByGeoArea = PlaceDAO.getInstance().getPlacesByGeoArea(place.getGeoArea(), 0, 10);

        assertTrue(placesByGeoArea.isPresent());
        assertEquals(0, placesByGeoArea.get().getPaginationOffset());
        assertEquals(1, placesByGeoArea.get().getCollection().size());
        for (Place p : placesByGeoArea.get().getCollection()) {
            assertEquals(place.getId(), p.getId());
        }

        boolean b1 = PlaceDAO.getInstance().deletePlaceById(place.getId());
        assertTrue(b1);
    }

    @Test
    public void testPaginationOfPlace() {
        Set<Place> places = new HashSet<>();
        for(int i = 0; i < 25; i++) {
            places.add(ModelCreators.createPlace());
        }
        places.forEach(PlaceDAO.getInstance()::storeOrUpdatePlace);
        places.forEach(p -> assertNotNull(p.getId()));

        Optional<PaginationWrapper<Place>> placesByGeoArea = PlaceDAO.getInstance().getPlacesByGeoArea(ModelCreators.createPlace().getGeoArea(), 0, 10);
        assertTrue(placesByGeoArea.isPresent());
        assertEquals(10, placesByGeoArea.get().getCollection().size());



        Optional<PaginationWrapper<Place>> placesByGeoArea1 = PlaceDAO.getInstance().getPlacesByGeoArea(ModelCreators.createPlace().getGeoArea(), 10, 10);
        assertTrue(placesByGeoArea1.isPresent());
        assertEquals(10,placesByGeoArea1.get().getCollection().size());

        Optional<PaginationWrapper<Place>> placesByGeoArea2 = PlaceDAO.getInstance().getPlacesByGeoArea(ModelCreators.createPlace().getGeoArea(), 20, 10);
        assertTrue(placesByGeoArea2.isPresent());
        assertEquals(5,placesByGeoArea2.get().getCollection().size());

        Set<Place> allResults = new HashSet<>();
        allResults.addAll(placesByGeoArea.get().getCollection());
        allResults.addAll(placesByGeoArea1.get().getCollection());
        allResults.addAll(placesByGeoArea2.get().getCollection());

        assertEquals(places.size(), allResults.size());


        places.forEach(p -> PlaceDAO.getInstance().deletePlaceById(p.getId()));
    }



}
