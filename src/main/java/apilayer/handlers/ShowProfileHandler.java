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


/**
 * Created by johnny on 2017-04-25.
 */

@Slf4j
public class ShowProfileHandler extends StaticFileTemplateHandler {
    /* ska ta in från user, kolla att user är inloggad*/


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
            try (Session session = HibernateUtil.getInstance().openSession()) {
                /*Hibernate.initialize(user.getInvites()); eventuellt använda för att hämta invite*/
                Map<String, Object> map = new HashMap<>();
                map.put("user", user);
                return Optional.of(map);

            } catch (Exception e) {
            log.info("hibernate error " + Arrays.toString(e.getStackTrace()));
            return Optional.empty();
        }
    }
}



