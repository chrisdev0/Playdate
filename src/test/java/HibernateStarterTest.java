import dblayer.HibernateSessionFactory;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HibernateStarterTest {

    private HibernateSessionFactory hibernateSessionFactory;

    @Before
    public void init() {
        try {
            hibernateSessionFactory = HibernateSessionFactory.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStartup() {
        assertNotEquals(null, hibernateSessionFactory);
    }

    @Test
    public void testSaveObject() {
        Place place = new Place("abc","a", "a", "image/testlekplats.png", "123", "123", 5, 5);
        assertNotEquals(-1L, doSave(place).longValue());
    }

    public Long doSave(Place place) {
        Long placeId = -1L;
        Session session = hibernateSessionFactory.getNewSession();
        Transaction tx = session.getTransaction();
        try {
            tx.begin();
            placeId = (Long) session.save(place);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            session.close();
            return placeId;
        }
    }


    @Test
    public void testLoadObject() {
        Session session = hibernateSessionFactory.getNewSession();
        Transaction tx = session.getTransaction();
        tx.begin();
        long placeId = -1L;
        Place toSave = new Place("def", "abd", "abc", "image/testlekplats.png", "123", "123", 10, 10);
        try {
            placeId = doSave(toSave);
            System.out.println(placeId);
            assertNotEquals(-1L, placeId);
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            assertEquals(false, true);
        } finally {
            session.close();
        }
        session = hibernateSessionFactory.getNewSession();
        tx = session.getTransaction();
        tx.begin();
        try {
            Place place = session.get(Place.class, placeId);
            assertEquals(place, toSave);
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            assertEquals(false, true);
        } finally {
            session.close();
        }
    }

}
