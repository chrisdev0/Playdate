package apilayer.handlers.templateHandlers;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.Paths;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
import model.Playdate;
import model.User;
import spark.Request;
import utils.ParserHelpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static spark.Spark.halt;

public class GetOnePlaydateHandler extends StaticFileTemplateHandlerImpl {

    public GetOnePlaydateHandler() {
        super("showplaydate.vm", 400, true);
    }


    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        String placeIdStr = request.queryParams(Paths.QueryParams.PLAYDATE_BY_ID);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
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
