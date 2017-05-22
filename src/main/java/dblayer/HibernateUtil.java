package dblayer;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import secrets.Secrets;

@Slf4j
public class HibernateUtil {

    private SessionFactory sessionFactory;

    private static HibernateUtil instance;

    public static HibernateUtil getInstance() {
        if (instance == null) {
            instance = new HibernateUtil();
        }
        return instance;
    }

    /** Hämtar username, password och URL till db från secrets.txt-filen
     *  Om alla värden finns så skapas databasen med hjälp av hibernate.cfg.xml
     *  i initConfig()
     * */
    private HibernateUtil() {
        try {
            sessionFactory = initConfig(Secrets.DB_HOST,Secrets.DB_USER,Secrets.DB_PASS);
        } catch (Exception e) {
            log.error("Error creating hibernate util", e);
            System.exit(-2);
        }
    }

    /**
     * Skapar SessionFactory med hibernate.cfg.xml och värdena från secrets.txt
     */
    private SessionFactory initConfig(String dbUrl, String username, String password) throws Exception {
        return new Configuration().configure("hibernate.cfg.xml")
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.url", dbUrl + Constants.HIBERNATE_DB_URL_SETTINGS)
                .buildSessionFactory();
    }

    /** Returnerar en ny session.
     *
     *  !!!!!!!!!!!
     *  GLÖM INTE ATT STÄNGA session med session.close()
     *  !!!!!!!!!!!
     *  Eller kör Try-With-Resources genom
     *  try (Session session = HibernateUtils.getInstance.openSession(){
     *      blabla
     *  }
     *
     * */
    public Session openSession() {
        return sessionFactory.openSession();
    }

    public void initializeField(Object o) {
        try (Session session = openSession()){
            Hibernate.initialize(o);
        }
    }

}
