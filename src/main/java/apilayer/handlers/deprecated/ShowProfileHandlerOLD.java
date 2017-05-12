package apilayer.handlers.deprecated;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandler;
import lombok.extern.slf4j.Slf4j;
import model.User;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.halt;


@Slf4j
@Deprecated
public class ShowProfileHandlerOLD extends StaticFileTemplateHandler {


    public ShowProfileHandlerOLD(String templateName, int onErrorHTTPStatusCode) throws IllegalArgumentException {
        super(templateName, onErrorHTTPStatusCode, true);

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
