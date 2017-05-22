package apilayer.handlers.templateHandlers;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.Paths;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
import model.Friendship;
import model.Playdate;
import model.PlaydateVisibilityType;
import model.User;
import secrets.Secrets;
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
        map.put("mapsapikey", Secrets.GOOGLE_MAPS_KEY);
        if (playdateById.isPresent()) {
            if (userShouldHaveAccessToPlaydate(playdateById.get(), user)) {
                map.put("playdate", playdateById.get());
                return Optional.of(map);
            } else {
                return Optional.of(onErrorPage("Du har inte tillgång till denna playdate"));
            }
        } else {
            return Optional.empty();
        }
    }

    private boolean userShouldHaveAccessToPlaydate(Playdate playdate, User user) {
        return playdate.getOwner().equals(user) || playdate.getPlaydateVisibilityType().equals(PlaydateVisibilityType.PUBLIC) || playdate.getPlaydateVisibilityType().equals(PlaydateVisibilityType.FRIENDS_ONLY) && usersAreFriends(playdate.getOwner(), user);
    }

    private boolean usersAreFriends(User user, User friend) {
        for (Friendship friendship : user.getFriends()) {
            if (friendship.getFriend().equals(friend) || friendship.getRequester().equals(friend)) {
                return true;
            }
        }
        return false;
    }
}
