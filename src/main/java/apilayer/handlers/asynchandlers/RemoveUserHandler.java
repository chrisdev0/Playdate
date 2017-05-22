package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.User;
import spark.Request;
import spark.Response;

import static apilayer.handlers.asynchandlers.SparkHelper.getUserFromSession;
import static apilayer.handlers.asynchandlers.SparkHelper.setStatusCodeAndReturnString;

@Slf4j
public class RemoveUserHandler {


    public static Object handleRemoveUser(Request request, Response response) {
        User user = getUserFromSession(request);
        if (UserDAO.getInstance().removeAllOfUser(user)) {
            log.info("removed all of user " + user.getName());
            request.session().invalidate();
            return setStatusCodeAndReturnString(response, 200, Constants.MSG.OK);
        } else {
            log.error("error removing user");
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.ERROR);
        }
    }


}
