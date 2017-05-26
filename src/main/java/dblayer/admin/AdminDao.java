package dblayer.admin;

import dblayer.HibernateUtil;
import model.Playdate;
import model.User;
import org.hibernate.Session;

import java.util.List;

public class AdminDao {

    private static AdminDao instance;


    private AdminDao(){}

    public static AdminDao getInstance() {
        if (instance == null) {
            instance = new AdminDao();
        }
        return instance;
    }


    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    public List<Playdate> getAllPlaydates() {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM Playdate", Playdate.class).list();
        }
    }






}
