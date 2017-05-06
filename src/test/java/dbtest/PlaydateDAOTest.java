package dbtest;

import dblayer.PlaceDAO;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
import model.Place;
import model.Playdate;
import model.User;
import org.junit.Test;
import testhelpers.HibernateTests;
import util.ModelCreators;

import java.util.Optional;

import static org.junit.Assert.*;
import static util.ModelCreators.remove;

public class PlaydateDAOTest extends HibernateTests {


    @Test
    public void testSavePlaydate() {
        User user = ModelCreators.createUser();
        Place place = ModelCreators.createPlace();
        Playdate playdate = ModelCreators.createPlaydate(user, place);
        boolean b = UserDAO.getInstance().saveUserOnLogin(user);
        assertTrue(b);

        boolean b1 = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b1);

        Optional<Playdate> playdateOptional = PlaydateDAO.getInstance().saveNewPlaydate(playdate, user);
        assertTrue(playdateOptional.isPresent());
        assertNotNull(playdate.getId());

        remove(user);
        remove(place);
        remove(playdate);
    }

    @Test
    public void testSaveIllegalPlaydate() {
        User user = ModelCreators.createUser();
        Place place = ModelCreators.createPlace();
        Playdate playdate = ModelCreators.createPlaydate(user, place);
        playdate.setHeader(null);
        boolean b = UserDAO.getInstance().saveUserOnLogin(user);
        assertTrue(b);

        boolean b1 = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b1);



        Optional<Playdate> playdateOptional = PlaydateDAO.getInstance().saveNewPlaydate(playdate, user);
        assertFalse(playdateOptional.isPresent());

        boolean b2 = PlaydateDAO.getInstance().deletePlaydate(playdate);
        assertFalse(b2);
        remove(user);
        remove(place);
    }

    @Test
    public void testUpdatePlaydate() {
        User user = ModelCreators.createUser();
        Place place = ModelCreators.createPlace();
        Playdate playdate = ModelCreators.createPlaydate(user, place);
        boolean b = UserDAO.getInstance().saveUserOnLogin(user);
        assertTrue(b);

        boolean b1 = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b1);

        Optional<Playdate> playdateOptional = PlaydateDAO.getInstance().saveNewPlaydate(playdate, user);
        assertTrue(playdateOptional.isPresent());
        assertNotNull(playdate.getId());

        playdate.setDescription("test");
        boolean b2 = PlaydateDAO.getInstance().updatePlaydate(playdate);
        assertTrue(b2);

        remove(user);
        remove(place);
        remove(playdate);
    }

    @Test
    public void testUpdatePlaydateError() {
        User user = ModelCreators.createUser();
        Place place = ModelCreators.createPlace();
        Playdate playdate = ModelCreators.createPlaydate(user, place);
        boolean b = UserDAO.getInstance().saveUserOnLogin(user);
        assertTrue(b);

        boolean b1 = PlaceDAO.getInstance().storeOrUpdatePlace(place);
        assertTrue(b1);

        Optional<Playdate> playdateOptional = PlaydateDAO.getInstance().saveNewPlaydate(playdate, user);
        assertTrue(playdateOptional.isPresent());
        assertNotNull(playdate.getId());

        Long playdateId = playdate.getId();

        playdate.setHeader(null);
        boolean b2 = PlaydateDAO.getInstance().updatePlaydate(playdate);
        assertFalse(b2);

        Optional<Playdate> playdateById = PlaydateDAO.getInstance().getPlaydateById(playdateId);
        assertTrue(playdateById.isPresent());
        assertNotNull(playdateById.get().getId());

        remove(user);
        remove(place);
        remove(playdateById.get());
    }




}
