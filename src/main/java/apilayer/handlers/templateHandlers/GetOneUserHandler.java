package apilayer.handlers.templateHandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.Paths;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
import model.Playdate;
import model.PlaydateVisibilityType;
import model.User;
import spark.Request;
import utils.ParserHelpers;

import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.halt;

public class GetOneUserHandler extends StaticFileTemplateHandlerImpl {

    public GetOneUserHandler() throws IllegalArgumentException {
        super("show-user.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        String userIdStr = request.queryParams(Paths.QueryParams.USER_ID);
        Long userid = ParserHelpers.parseToLong(userIdStr);

        Optional<User> userById = UserDAO.getInstance().getUserById(userid);
        if (userById.isPresent()) {
            map.put("other-user", userById.get());
            Optional<List<Playdate>> playdateByOwnerId = PlaydateDAO.getInstance().getPlaydateByOwnerId(userById.get().getId());
            if (playdateByOwnerId.isPresent()) {
                List<Playdate> playdates = playdateByOwnerId.get();
                playdates.forEach(playdate -> playdate.getPlace().setComments(null));
                Set<Playdate> collect = playdates.stream().filter(playdate -> playdate.getPlaydateVisibilityType().equals(PlaydateVisibilityType.PUBLIC)).collect(Collectors.toSet());
                map.put("playdates", collect);
            }
        } else {
            throw halt(400);
        }
        return Optional.of(map);
    }
}
