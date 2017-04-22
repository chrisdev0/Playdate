package dblayer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateSessionFactory {

    private static HibernateSessionFactory instance;
    private SessionFactory sessionFactory;


    private HibernateSessionFactory() throws Exception {
        initHibernate();
    }

    private void initHibernate() throws Exception {
        if (sessionFactory == null) {
            HibernateStarter hibernateStarter = new HibernateStarter();
            sessionFactory = hibernateStarter.initConfig();
        }
    }

    public static HibernateSessionFactory getInstance() throws Exception {
        if (instance == null) {
            instance = new HibernateSessionFactory();
        }
        return instance;
    }

    public Session getNewSession() {
        return sessionFactory.openSession();
    }

}
