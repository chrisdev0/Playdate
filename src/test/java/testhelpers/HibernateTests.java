package testhelpers;

import dblayer.HibernateStarter;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class HibernateTests {

    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        HibernateStarter hibernateStarter = new HibernateStarter();
        sessionFactory = hibernateStarter.initConfig();
    }


    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
