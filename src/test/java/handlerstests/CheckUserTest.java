package handlerstests;
import apilayer.handlers.LoginTryHandler;
import dblayer.HibernateStarter;
import model.Gender;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.HaltException;
import testhelpers.HibernateTests;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class CheckUserTest extends HibernateTests {

    private static Set<User> testUsers = new HashSet<>();

    @BeforeClass
    public static void setUp() throws Exception{
        HibernateTests.setUp();
        User user0 = new User("123", "Erik", "erik@mail.com", "password", "123", "a", Gender.POJKE);
        User user2 = new User("124", "Lina", "lina@mail.com", "åäö", "123", "a", Gender.FLICKE);
        User user3 = new User("125", "Henrik", "henrik@mail.com", "หนาว", "2", "a", Gender.ANNAT);
        User user4 = new User("125", "Henrik", "你好@mail.com", "หนาว1234", "13", "a", Gender.FLICKE);
        testUsers.add(user0);
        testUsers.add(user2);
        testUsers.add(user3);
        testUsers.add(user4);
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            testUsers.forEach(user -> {
                Long id = (Long) session.save(user);
                user.setId(id);
            });
            tx.commit();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument1() {
        LoginTryHandler loginTryHandler = new LoginTryHandler(getSessionFactory());
        loginTryHandler.checkEmailAndPasswordExist("", "abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument2() {
        LoginTryHandler loginTryHandler = new LoginTryHandler(getSessionFactory());
        loginTryHandler.checkEmailAndPasswordExist("abc", "");
    }


    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument3() {
        LoginTryHandler loginTryHandler = new LoginTryHandler(getSessionFactory());
        loginTryHandler.checkEmailAndPasswordExist("abc", null);
    }



    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument4() {
        LoginTryHandler loginTryHandler = new LoginTryHandler(getSessionFactory());
        loginTryHandler.checkEmailAndPasswordExist(null, "abc");
    }

    @Test
    public void testUserExists() {
        LoginTryHandler loginTryHandler = new LoginTryHandler(getSessionFactory());
        for (User user : testUsers) {
            String email = user.getEmail();
            String password = user.getPassword();
            Optional<User> userOpt = loginTryHandler.checkEmailAndPasswordExist(email, password);
            assertTrue(userOpt.isPresent());
            System.out.println(user.toString() + " compared to " + userOpt.get().toString());
            assertTrue(user.equals(userOpt.get()));
        }
    }

    @Test
    public void testUserNotExists() {
        LoginTryHandler loginTryHandler = new LoginTryHandler(getSessionFactory());
        Optional<User> userOptional = loginTryHandler.checkEmailAndPasswordExist("poasdpoasd", "piopq+o2i2");
        assertEquals(false, userOptional.isPresent());
    }



    @AfterClass
    public static void deleteTestUsers(){
        if (getSessionFactory() != null) {
            try (Session session = getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                for (User user : testUsers) {
                    session.delete(user);
                }
                tx.commit();
            }
        }
    }
}
