package apilayer.handlers.templateHandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.SparkHelper;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
import model.Playdate;
import model.PlaydateVisibilityType;
import model.User;
import spark.Request;
import utils.ParserHelpers;
import utils.filters.TimeFilterable;

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
        String userIdStr = request.queryParams(Paths.QueryParams.USER_BY_ID);
        Long userid = ParserHelpers.parseToLong(userIdStr);

        Optional<User> userById = UserDAO.getInstance().getUserById(userid);
        if (userById.isPresent()) {
            map.put("other-user", userById.get());
            Optional<List<Playdate>> playdateByOwnerId = PlaydateDAO.getInstance().getPlaydateByOwnerId(userById.get().getId(), TimeFilterable.TimeFilter.ALL);
            if (playdateByOwnerId.isPresent()) {
                List<Playdate> playdates = playdateByOwnerId.get();
                playdates.forEach(playdate -> playdate.getPlace().setComments(null));
                Set<Playdate> collect = playdates.stream().filter(playdate -> playdate.getPlaydateVisibilityType().equals(PlaydateVisibilityType.PUBLIC)).filter(Playdate::playdateIsInFuture)
                        .collect(Collectors.toSet());
                map.put("playdates", collect);
                map.put("friendsetting", UserDAO.getInstance().getFriendState(SparkHelper.getUserFromSession(request), userById.get()));
            }
        } else {
            throw halt(400);
        }
        return Optional.of(map);
    }
}
