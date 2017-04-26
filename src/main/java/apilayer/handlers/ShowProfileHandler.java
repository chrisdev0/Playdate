package apilayer.handlers;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandler;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.halt;


@Slf4j
public class ShowProfileHandler extends StaticFileTemplateHandler {


    public ShowProfileHandler(String templateName, int onErrorHTTPStatusCode) throws IllegalArgumentException {
        super(templateName, onErrorHTTPStatusCode);

    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
            User user = request.session().attribute(Constants.USER_SESSION_KEY);
            if (user == null) {
                log.error("User is null");
                throw halt(400, "user is null");
            }
                Map<String, Object> map = new HashMap<>();
                map.put("user", user);
                return Optional.of(map);
    }
}
