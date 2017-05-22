package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import com.google.gson.GsonBuilder;
import dblayer.InviteDAO;
import dblayer.PlaydateDAO;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.Invite;
import model.Playdate;
import model.PlaydateVisibilityType;
import model.User;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;

import java.util.List;
import java.util.Optional;
import static apilayer.handlers.asynchandlers.SparkHelper.*;

@Slf4j
public class AttendanceInviteHandler {


    public static Object handleAttendPublicPlaydate(Request request, Response response) {
        String playdateIdStr = request.queryParams(Paths.QueryParams.PLAYDATE_BY_ID);
        Long playdateId = ParserHelpers.parseToLong(playdateIdStr);
        Optional<Playdate> playdateById = PlaydateDAO.getInstance().getPlaydateById(playdateId);
        if (playdateById.isPresent()) {
            Playdate playdate = playdateById.get();
            if (playdate.getPlaydateVisibilityType().equals(PlaydateVisibilityType.PUBLIC)) {
                if (PlaydateDAO.getInstance().addAttendance(request.session().attribute(Constants.USER_SESSION_KEY), playdate)) {
                    response.status(200);
                    return "";
                } else {
                    return setStatusCodeAndReturnString(response, 400, Constants.MSG.ERROR);
                }
            } else {
                return setStatusCodeAndReturnString(response, 400, Constants.MSG.PLAYDATE_IS_NOT_PUBLIC);
            }
        }
        return setStatusCodeAndReturnString(response, 400, Constants.MSG.NO_PLAYDATE_WITH_ID);
    }

    public static Object handleAcceptInviteToPlaydate(Request request, Response response) {
        String inviteIdStr = request.queryParams(Paths.QueryParams.INVITE_BY_ID);
        Long inviteId = ParserHelpers.parseToLong(inviteIdStr);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        Optional<Invite> inviteById = InviteDAO.getInstance().getInviteById(inviteId);
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

    public static Object handleSendInviteToPlaydate(Request request, Response response) {
        String sendToUserStr = request.queryParams(Paths.QueryParams.USER_BY_ID);
        String playDateIdStr = request.queryParams(Paths.QueryParams.PLAYDATE_BY_ID);
        Optional<User> sendToUser = UserDAO.getInstance().getUserById(ParserHelpers.parseToLong(sendToUserStr));
        if (sendToUser.isPresent()) {
            Optional<Playdate> playdateOptional = PlaydateDAO.getInstance().getPlaydateById(ParserHelpers.parseToLong(playDateIdStr));
            if (playdateOptional.isPresent()) {
                if (playdateOptional.get().getOwner().equals(getUserFromSession(request))) {
                    Optional<Invite> inviteOfUserAndPlaydate = InviteDAO.getInstance().getInviteOfUserAndPlaydate(sendToUser.get(), playdateOptional.get());
                    if (!inviteOfUserAndPlaydate.isPresent()) {
                        if (InviteDAO.getInstance().addInviteToUserAndPlaydate(new Invite(playdateOptional.get(), sendToUser.get()))) {
                            return "";
                        } else {
                            log.info("couldn't send invite to user " + sendToUserStr);
                            response.status(400);
                            return Constants.MSG.ERROR;
                        }
                    } else {
                        return setStatusCodeAndReturnString(response, 400, Constants.MSG.ALREADY_INVITED);
                    }
                } else {
                    response.status(400);
                    return Constants.MSG.USER_IS_NOT_OWNER_OF_PLAYDATE;
                }
            } else {
                response.status(400);
                return Constants.MSG.NO_PLAYDATE_WITH_ID;
            }
        } else {
            response.status(400);
            return Constants.MSG.NO_USER_WITH_ID;
        }
    }


    public static Object getInvitesOfLoggedInUser(Request request, Response response) {
        User user = getUserFromSession(request);
        Optional<List<Invite>> invitesOfUser = InviteDAO.getInstance().getInvitesOfUser(user);
        if (invitesOfUser.isPresent()) {
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(invitesOfUser.get());
        }
        response.status(400);
        return Constants.MSG.ERROR;
    }


    public static Object handleDeclineInviteToPlaydate(Request request, Response response) {
        Long inviteId = ParserHelpers.parseToLong(request.queryParams(Paths.QueryParams.INVITE_BY_ID));
        Optional<Invite> inviteById = InviteDAO.getInstance().getInviteById(inviteId);
        if (inviteById.isPresent()) {
            User user = getUserFromSession(request);
            if (inviteById.get().getInvited().equals(user)) {
                if (InviteDAO.getInstance().removeInvite(inviteById.get(), inviteById.get().getPlaydate(), user)) {
                    return "";
                } else {
                    return setStatusCodeAndReturnString(response, 400, Constants.MSG.ERROR);
                }
            } else {
                return setStatusCodeAndReturnString(response, 400, Constants.MSG.USER_IS_NOT_OWNER_OF_INVITE);
            }
        } else {
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.NO_INVITE_WITH_ID);
        }
    }
}
