package testhelpers;

import dblayer.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.BeforeClass;

public class HibernateTests {

    private static HibernateUtil hibernateUtil;

    @BeforeClass
    public static void setUp() {
        hibernateUtil = HibernateUtil.getInstance();
    }


    public static Session openSession() {
        return hibernateUtil.openSession();
    }
}
