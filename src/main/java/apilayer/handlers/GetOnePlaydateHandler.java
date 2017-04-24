package apilayer.handlers;

import apilayer.StaticFileTemplateHandler;
import dblayer.HibernateUtil;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import model.Playdate;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by johnny on 2017-04-24.
 */

@Slf4j
public class GetOnePlaydateHandler extends StaticFileTemplateHandler {
    private Long playdateId;

    public GetOnePlaydateHandler(String templateName, long playdateId, int onErrorHTTPStatusCode) throws IllegalArgumentException {
        super(templateName,onErrorHTTPStatusCode);
        this.playdateId = playdateId;
    }

    @Override
    public Optional<Map<String, Object>> createModelMap() {
        try {
            try (Session session = HibernateUtil.getInstance().openSession()) {
                Playdate playdate = session.get(Playdate.class, playdateId);
                log.info("Found playdate with id " + playdateId +" = " + (playdate != null));
                Map<String, Object> map = new HashMap<>();
                map.put("playdate", playdate);
                return Optional.of(map);
            }
        } catch (Exception e) {
            log.info("hibernate error ", e);
            return Optional.empty();
        }
    }
}
