package handlerstests;
import apilayer.handlers.LoginHandler;
import model.Gender;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import testhelpers.HibernateTests;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class CheckUserTest extends HibernateTests {

    private static Set<User> testUsers = new HashSet<>();

    @BeforeClass
    public static void setUp(){
        HibernateTests.setUp();
        User user0 = new User("Erik", "erik@mail.com", null, "123", "a", Gender.MALE);
        User user2 = new User("Lina", "lina@mail.com", null, "123", "a", Gender.FEMALE);
        User user3 = new User("Henrik", "henrik@mail.com", null, "2", "a", Gender.OTHER);
        User user4 = new User("Henrik", "你好@mail.com", null, "13", "a", Gender.FEMALE);
        user0 = User.createUserHelper(user0, "test");
        user2 = User.createUserHelper(user2, "าส่ฟกหวสาๆบไยำงวสาว");
        user3 = User.createUserHelper(user3, "你好123123123");
        user4 = User.createUserHelper(user4, "sadasdl23;1l23;l>\\");
        testUsers.add(user0);
        testUsers.add(user2);
        testUsers.add(user3);
        testUsers.add(user4);
        try (Session session = openSession()) {
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
        LoginHandler loginHandler = new LoginHandler();
        loginHandler.checkEmailAndPasswordExist("", "abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument2() {
        LoginHandler loginHandler = new LoginHandler();
        loginHandler.checkEmailAndPasswordExist("abc", "");
    }


    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument3() {
        LoginHandler loginHandler = new LoginHandler();
        loginHandler.checkEmailAndPasswordExist("abc", null);
    }



    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument4() {
        LoginHandler loginHandler = new LoginHandler();
        loginHandler.checkEmailAndPasswordExist(null, "abc");
    }


    @Test
    public void testUserNotExists() {
        LoginHandler loginHandler = new LoginHandler();
        Optional<User> userOptional = loginHandler.checkEmailAndPasswordExist("poasdpoasd", "piopq+o2i2");
        assertEquals(false, userOptional.isPresent());
    }



    @AfterClass
    public static void deleteTestUsers(){
        if (openSession() != null) {
            try (Session session = openSession()) {
                Transaction tx = session.beginTransaction();
                for (User user : testUsers) {
                    session.delete(user);
                }
                tx.commit();
            }
        }
    }
}
