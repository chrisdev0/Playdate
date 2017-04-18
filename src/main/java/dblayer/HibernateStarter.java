package dblayer;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import secrets.Secrets;
import secrets.SecretsParserException;

import java.util.Optional;

public class HibernateStarter {

    private final String username;
    private final String password;
    private final String dbUrl;

    public HibernateStarter() throws Exception {
        Secrets secrets = Secrets.getInstance().loadSecretsFile("secrets.txt");
        Optional<String> usernameOpt = secrets.getValue("dbUserName");
        Optional<String> passwordOpt = secrets.getValue("dbPassword");
        Optional<String> dbUrlOpt = secrets.getValue("dbName");

        if (usernameOpt.isPresent() && passwordOpt.isPresent() && dbUrlOpt.isPresent()) {
            password = passwordOpt.get();
            dbUrl = dbUrlOpt.get();
            username = usernameOpt.get();
        } else {
            throw new SecretsParserException("Missing Database values");
        }
    }


    public SessionFactory initConfig() throws Exception{
        return new Configuration().configure("hibernate.cfg.xml")
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.url", dbUrl)
                .buildSessionFactory();
    }
}
