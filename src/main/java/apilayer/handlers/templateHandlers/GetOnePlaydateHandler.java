package apilayer.handlers.templateHandlers;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.Paths;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
import model.*;
import presentable.frontend.UserPlaydateRelationship;
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
                map.put("playdatesetting", getPlaydateSetting(user, playdateById.get()));
                return Optional.of(map);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private int getPlaydateSetting(User user, Playdate playdate) {
        if (playdate.getOwner().equals(user)) {
            return UserPlaydateRelationship.USER_IS_OWNER.getNr();
        }
        if (playdate.getParticipants().contains(user)) {
            return UserPlaydateRelationship.USER_IS_ATTENDING.getNr();
        }
        if (playdate.getInvites().stream().map(Invite::getInvited).filter(user1 -> user1.equals(user)).count() == 1) {
            return UserPlaydateRelationship.USER_IS_INVITED.getNr();
        }
        return UserPlaydateRelationship.USER_CAN_JOIN.getNr();
    }

    private boolean userShouldHaveAccessToPlaydate(Playdate playdate, User user) {
        if (playdate.getOwner().equals(user) || playdate.getPlaydateVisibilityType().equals(PlaydateVisibilityType.PUBLIC)) {
            return true;
        }
        if (playdate.getPlaydateVisibilityType().equals(PlaydateVisibilityType.FRIENDS_ONLY)) {
            if (usersAreFriends(playdate.getOwner(), user)) {
                return true;
            } else {
                return false;
            }
        }
        if (playdate.getPlaydateVisibilityType().equals(PlaydateVisibilityType.PRIVATE)) {
            if (playdate.getParticipants().contains(user) || userIsInviteToPlaydate(user, playdate)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean userIsInviteToPlaydate(User user, Playdate playdate) {
        return playdate.getInvites().stream().map(Invite::getInvited).filter(user1 -> user1.equals(user)).count() == 1;
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
