import lombok.extern.slf4j.Slf4j;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import testhelpers.HibernateTests;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class HibernateUtilTest extends HibernateTests {

    private static List<Object> testData = new ArrayList<>();
    private static Place place1, toSave;

    @BeforeClass
    public static void initPlace() {
        place1 = new Place("abcgjhgkhgh", "a", "image/testlekplats.png", "123", "123", 5, 5, "");
        toSave = new Place("defhgkkhjgjhgkh", "abd", "image/testlekplats.png", "123", "123", 10, 10, "");
        testData.add(place1);
        testData.add(toSave);
    }


    @Test
    public void testStartup() {
        Session session = openSession();
        assertNotEquals(null, session);
        session.close();
    }

    @Test
    public void testSaveObject() {
        assertNotEquals(-1L, doSave(place1).longValue());
    }

    @Test(expected = Exception.class)
    public void testSaveObjectTwice() {
        Session session = openSession();
        session.getTransaction().begin();
        session.save(place1);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        session.save(place1);
        session.getTransaction().commit();
        session.close();
    }

    public Long doSave(Place place) {
        Long placeId = -1L;
        Transaction tx = null;
        try (Session session = openSession()){
            tx = session.beginTransaction();
            placeId = (Long) session.save(place);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        }
        return placeId;
    }


    @Test
    public void testLoadObject() {
        long placeId = -1L;
        Transaction tx = null;
        try (Session session = openSession()) {
            tx = session.beginTransaction();
            placeId = doSave(toSave);
            System.out.println(placeId);
            assertNotEquals(-1L, placeId);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
            if (tx != null) {
                tx.rollback();
            }
        }

        try (Session session = openSession()){
            Place place = session.get(Place.class, placeId);
            assertEquals(place, toSave);
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void removePlaces() {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            log.info("removing " + testData.size() + " objects");
            testData.forEach(session::remove);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
