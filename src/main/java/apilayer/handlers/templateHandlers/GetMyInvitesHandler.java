package apilayer.handlers.templateHandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.asynchandlers.SparkHelper;
import dblayer.InviteDao;
import model.Invite;
import spark.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GetMyInvitesHandler extends StaticFileTemplateHandlerImpl {

    public GetMyInvitesHandler() {
        super("my-invite.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        Optional<List<Invite>> invitesOfUser = InviteDao.getInstance().getInvitesOfUser(SparkHelper.getUserFromSession(request));
        if (invitesOfUser.isPresent()) {
            map.put("invites", invitesOfUser.get());
        } else {
            Optional.empty();
        }
        return Optional.of(map);
    }
}
