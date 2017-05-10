package apilayer.handlers.templateHandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.Playdate;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class EditPlaydateHandler extends StaticFileTemplateHandlerImpl {

    public EditPlaydateHandler() {
        super("createplaydate.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        String playdateIdStr = request.queryParams("playdateId");
        Long playdateId = ParserHelpers.parseToLong(playdateIdStr);
        Optional<Playdate> playdateById = PlaydateDAO.getInstance().getPlaydateById(playdateId);
        if (playdateById.isPresent()) {
            log.info("found playdate");
            playdateById.get().setParticipants(null);
            playdateById.get().setInvites(null);
            playdateById.get().getPlace().setComments(null);
            map.put("playdate", playdateById.get());
            log.info("map contents");
            map.forEach((s, o) -> log.info("key= " + s + " value= " + o));
            return Optional.of(map);
        } else {
            return Optional.empty();
        }
    }
}
