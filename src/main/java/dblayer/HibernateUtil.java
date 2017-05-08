package dblayer;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import secrets.Secrets;
import secrets.SecretsParserException;

import java.io.IOException;
import java.util.Optional;

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
            Secrets secrets = Secrets.getInstance().loadSecretsFile("secrets.txt");
            Optional<String> usernameOpt = secrets.getValue("dbUserName");
            Optional<String> passwordOpt = secrets.getValue("dbPassword");
            Optional<String> dbUrlOpt = secrets.getValue("dbName");
            if (usernameOpt.isPresent() && passwordOpt.isPresent() && dbUrlOpt.isPresent()) {
                sessionFactory = initConfig(dbUrlOpt.get(), usernameOpt.get(), passwordOpt.get());
            } else {
                throw new SecretsParserException("Missing Database values");
            }
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
                .setProperty("hibernate.connection.url", dbUrl)
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
