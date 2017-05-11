package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import dblayer.InviteDao;
import dblayer.PlaydateDAO;
import lombok.extern.slf4j.Slf4j;
import model.Invite;
import model.Playdate;
import model.PlaydateVisibilityType;
import model.User;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;

import java.util.Optional;

@Slf4j
public class AttendanceHandler {


    public static Object handleAttendPublicPlaydate(Request request, Response response) {
        String playdateIdStr = request.queryParams(Paths.QueryParams.GET_ONE_PLAYDATE_BY_ID);
        Long playdateId = ParserHelpers.parseToLong(playdateIdStr);
        Optional<Playdate> playdateById = PlaydateDAO.getInstance().getPlaydateById(playdateId);
        if (playdateById.isPresent()) {
            Playdate playdate = playdateById.get();
            if (playdate.getPlaydateVisibilityType().equals(PlaydateVisibilityType.PUBLIC)) {
                if (PlaydateDAO.getInstance().addAttendance(request.session().attribute(Constants.USER_SESSION_KEY), playdate)) {
                    response.status(200);
                    return "";
                } else {
                    response.status(400);
                    return "error";
                }
            } else {
                response.status(400);
                return "playdate_not_public";
            }
        }
        response.status(400);
        return "no_playdate";
    }

    public static Object handleAcceptInviteToPlaydate(Request request, Response response) {
        String inviteIdStr = request.queryParams("inviteId");
        Long inviteId = ParserHelpers.parseToLong(inviteIdStr);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        Optional<Invite> inviteById = InviteDao.getInstance().getInviteById(inviteId);
        if (inviteById.isPresent()) {
            if (inviteById.get().getInvited().equals(user)) {
                if (PlaydateDAO.getInstance().acceptAndAddAttendance(inviteById.get())) {
                    response.status(200);
                    return "";
                } else {
                    log.error("error accepting invite and saving");
                }
            } else {
                log.error("tried to accept invite of other user");
            }
        } else {
            log.error("no invite with this id");
        }
        response.status(400);
        return "error";
    }



}
