package testutils;

import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import model.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import secrets.Secrets;

@Slf4j
public class TestStarter {

    private static boolean isInit = false;
    private static boolean failedInit = true;

    static {
        try {
            if (!isInit) {
                Secrets.initSecrets(true);
                failedInit = false;
                isInit = true;
            }
        } catch (Exception e) {
            log.info("failed to init env vars");
        }
    }

    @BeforeClass
    public static void checkInitialized() {
        if (failedInit) {
            org.junit.Assert.fail("init of env vars failed");
        }
    }

    @AfterClass
    public static void deleteAllTestData() {
        User testUser = ModelCreators.createUser();
        Place testPlace = ModelCreators.createPlace();
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getInstance().openSession();
            transaction = session.beginTransaction();

            session.createQuery("DELETE FROM User WHERE name = :name")
                    .setParameter("name", testUser.getName()).executeUpdate();
            session.createQuery("DELETE FROM Place p where p.category = :cat")
                    .setParameter("cat", testPlace.getCategory()).executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            log.error("error removing data", e);
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}
