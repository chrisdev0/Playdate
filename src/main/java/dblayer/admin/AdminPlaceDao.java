package dblayer.admin;

import apilayer.handlers.adminhandlers.AdminPlaceHandler;
import dblayer.HibernateUtil;
import model.Place;
import org.hibernate.Session;

import java.util.List;

public class AdminPlaceDao {

    private static AdminPlaceDao instance;

    private AdminPlaceDao(){}

    public static AdminPlaceDao getInstance() {
        if (instance == null) {
            instance = new AdminPlaceDao();
        }
        return instance;
    }

    public List<Place> getAllPlaces() {
        try (Session session = HibernateUtil.getInstance().openSession()) {
            return session.createQuery("FROM Place ", Place.class).list();
        }
    }


}
