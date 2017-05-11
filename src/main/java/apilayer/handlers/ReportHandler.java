package apilayer.handlers;

import apilayer.Constants;
import dblayer.ReportDAO;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.User;
import spark.*;
import utils.ParserHelpers;

import java.util.Optional;

import static spark.Spark.halt;

/**
 * Created by martinsenden on 2017-05-11.
 */
@Slf4j
public class ReportHandler {

    /*
    Skapa userReport
     */

    public Object createUserReport(Request request, Response response){
        String reportedUserIdParam = request.queryParams("userId");
        String reportDescription = request.queryParams("reportDescription");
        Long reportedUserId = ParserHelpers.parseToLong(reportedUserIdParam);
        User user = request.session().attribute(Constants.USER_SESSION_KEY);

        if (user == null || user.getId().equals(reportedUserId)){
            log.error("user is null or same user as reported");
            throw halt(400);
        }

        Optional<User> reportedUser = UserDAO.getInstance().getUserById(reportedUserId);

        if (!reportedUser.isPresent()){
            log.error("User does not exist");
            throw halt(400);
        }


        if (reportedUser.isPresent()){
            if(ReportDAO.createUserReport(user, reportedUser.get(), reportDescription).isPresent()){
                log.info("Report has been sent");
                response.status(200);
                return "";
            }
        }

        throw halt(400, "User was not reported due to an error");
    }
}
