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
        Place place = new Place("abc", "a", "123", "123", 5, 5);
        assertEquals(true, doSave(place));
    }

    public boolean doSave(Place place) {
        Session session = hibernateSessionFactory.getNewSession();
        Transaction tx = session.getTransaction();
        try {
            tx.begin();
            session.save(place);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            return false;
        } finally {
            session.close();
            return true;
        }
    }


    @Test
    public void testLoadObject() {
        Session session = hibernateSessionFactory.getNewSession();
        Transaction tx = session.getTransaction();
        tx.begin();
        Place toSave = new Place("def", "abc", "123", "123", 10, 10);;
        try {
            assertEquals(true, doSave(toSave));
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
            Place place = session.get(Place.class, "def");
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
