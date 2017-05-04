package dbtest;

import dblayer.HibernateUtil;
import dblayer.PlaydateDAO;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestModel {

    private static HibernateUtil hibernateUtil;
    private static List<Object> objectsToRemove = new ArrayList<>();
    private static User attendingUser, ownerUser;

    @BeforeClass
    public static void setUp() {
        hibernateUtil = HibernateUtil.getInstance();
        ownerUser = new User("test", "test@gmail.com");
        Place place = new Place("123abcdef-1-1", "testplatsen", "test", "123", "123", 1, 1, "shortcd");
        Playdate playdate = new Playdate("titel", "desc", 1234123, ownerUser, place, PlaydateVisibilityType.PUBLIC);
        attendingUser = new User("test", "test@2.com");
        playdate.addParticipant(attendingUser);
        attendingUser.attendPlaydate(playdate);
        objectsToRemove.add(playdate);
        objectsToRemove.add(ownerUser);
        objectsToRemove.add(place);
        objectsToRemove.add(attendingUser);
        try (Session session = hibernateUtil.openSession()) {
            Transaction tx = session.beginTransaction();

            playdate.setId((Long)session.save(playdate));
            ownerUser.setId((Long)session.save(ownerUser));
            attendingUser.setId((Long)session.save(attendingUser));
            session.save(place);

            tx.commit();
        }
    }

    @Test
    public void testGetAttending() {
        boolean loopRun = false;
        Set<Playdate> playdateWhoUserIsAttending = PlaydateDAO.getInstance().getPlaydateWhoUserIsAttending(attendingUser);
        for (Playdate playdate : playdateWhoUserIsAttending) {
            loopRun = true;
            assertEquals("titel", playdate.getHeader());
        }
        assertTrue(loopRun);
    }

    @Test
    public void testGetAttendingAsOwner() {
        boolean loopRun = false;
        Set<Playdate> playdateWhoUserIsAttending = PlaydateDAO.getInstance().getPlaydateWhoUserIsAttending(ownerUser);
        for (Playdate playdate : playdateWhoUserIsAttending) {
            loopRun = true;
            assertEquals("titel", playdate.getHeader());
        }
        assertTrue(loopRun);
    }

    @Test
    public void testGetAttendingOwner() {
        List<Playdate> playdates = PlaydateDAO.getInstance().getPlaydateByOwnerId(ownerUser.getId());
        assertEquals("titel", playdates.get(0).getHeader());
    }


    @AfterClass
    public static void runDown() {
        try (Session session = hibernateUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            objectsToRemove.forEach(session::remove);
            tx.commit();
        }
    }

}
