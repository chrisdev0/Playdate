package apilayer.handlers.adminhandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import dblayer.HibernateUtil;
import model.Comment;
import model.Report;
import org.hibernate.Session;
import spark.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdminReportPageHandler extends StaticFileTemplateHandlerImpl {

    public AdminReportPageHandler() {
        super("/admin/adminReports.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        map.put("reports", getAllReports());
        return Optional.of(map);
    }

    public List<Report> getAllReports(){
        try (Session session = HibernateUtil.getInstance().openSession()){
            return session.createQuery("FROM Report ORDER BY createdAt DESC ", Report.class).list();
        }
    }
}
