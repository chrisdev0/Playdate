import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import testhelpers.HibernateTests;

import static org.junit.Assert.*;

public class HibernateUtilTest extends HibernateTests {


    @Test
    public void testStartup() {
        Session session = openSession();
        assertNotEquals(null, session);
        session.close();
    }

    @Test
    public void testSaveObject() {
        Place place = new Place("abc","a", "a", "image/testlekplats.png", "123", "123", 5, 5, "");
        assertNotEquals(-1L, doSave(place).longValue());
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
        Place toSave = new Place("def", "abd", "abc", "image/testlekplats.png", "123", "123", 10, 10, "");
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

}
