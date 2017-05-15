package dblayer;

import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class ReportDAO {

    private static ReportDAO instance;

    public static ReportDAO getInstance(){
        if (instance == null){
            instance = new ReportDAO();
        }
        return instance;
    }

    public Optional<Report> createUserReport(User reporter, User reportedUser, String reportDescription ){
        Optional<Report> ret = Optional.empty();
        Transaction tx = null;
        Session session = null;
        Report report = new Report(reporter, reportedUser, reportDescription);

        try {
            session = HibernateUtil.getInstance().openSession();
            tx = session.beginTransaction();
            session.save(report);
            session.update(reporter);
            session.update(reportedUser);
            tx.commit();
            ret = Optional.of(report);
        } catch(Exception e){
            if (tx != null){
                tx.rollback();
            }
        } finally{
            if (session != null) {
                session.close();
            }
        }

        return ret;
    }
}
