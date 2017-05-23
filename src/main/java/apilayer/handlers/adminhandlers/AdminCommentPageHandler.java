package apilayer.handlers.adminhandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import dblayer.HibernateUtil;
import model.Comment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdminCommentPageHandler extends StaticFileTemplateHandlerImpl {

    public AdminCommentPageHandler() {
        super("/admin/adminComments.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        map.put("comments", getAllComments());
        return Optional.of(map);
    }

    public List<Comment> getAllComments(){
        try (Session session = HibernateUtil.getInstance().openSession()){
            return session.createQuery("FROM Comment ORDER BY commentDate DESC ", Comment.class).list();
        }
    }
}
