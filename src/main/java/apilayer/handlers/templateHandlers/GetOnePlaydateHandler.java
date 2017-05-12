package apilayer.handlers.templateHandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.Paths;
import dblayer.PlaydateDAO;
import model.Playdate;
import spark.Request;
import utils.ParserHelpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.halt;

public class GetOnePlaydateHandler extends StaticFileTemplateHandlerImpl {

    public GetOnePlaydateHandler() {
        super("showplaydate.vm", 400, true);
    }


    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        String placeIdStr = request.queryParams(Paths.QueryParams.PLAYDATE_BY_ID);
        Long playdateId = ParserHelpers.parseToLong(placeIdStr);
        Optional<Playdate> playdateById = PlaydateDAO.getInstance().getPlaydateById(playdateId);
        Map<String, Object> map = new HashMap<>();
        if (playdateById.isPresent()) {
            map.put("playdate", playdateById.get());
        } else {
            throw halt(400);
        }
        return Optional.of(map);
    }
}
