package apilayer.handlers.templateHandlers;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandlerImpl;
import dblayer.UserDAO;
import model.User;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GetMyFriendsHandler extends StaticFileTemplateHandlerImpl {

    public GetMyFriendsHandler() {
        super("show-friends.vm", 400, true);
    }


    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        User user = request.session().attribute(Constants.USER_SESSION_KEY);

        Optional<Set<User>> friendsOfUser = UserDAO.getInstance().getFriendsOfUser(user);
        Optional<Set<User>> friendshipRequestOfUserTEMP = UserDAO.getInstance().getFriendshipRequestOfUserTEMP(user);

        if (friendsOfUser.isPresent() && friendshipRequestOfUserTEMP.isPresent()) {
            map.put("friends", friendsOfUser.get());
            map.put("friendsrequest", friendshipRequestOfUserTEMP.get());
            return Optional.of(map);
        }
        return Optional.empty();
    }

}
