package testhelpers;

import com.github.javafaker.Faker;
import dblayer.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

public class HibernateTests {

    private static HibernateUtil hibernateUtil;
    public Session session;
    public static Faker faker;


    @BeforeClass
    public static void setUp() {
        faker = new Faker();
        hibernateUtil = HibernateUtil.getInstance();
    }

    @Before
    public void beginTransaction() {
        session = hibernateUtil.openSession();
        session.beginTransaction();
    }

    @After
    public void rollbackTransaction() {
        if (session.getTransaction().isActive()) {
            session.getTransaction().rollback();
        }
        if (session.isOpen()) {
            session.close();
        }
    }


}
