package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import dblayer.ReportDAO;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.Report;
import model.User;
import spark.*;
import utils.ParserHelpers;

import java.util.Optional;

import static apilayer.Constants.MSG.*;
import static spark.Spark.halt;

import static apilayer.handlers.asynchandlers.SparkHelper.*;

@Slf4j
public class ReportHandler {


    public static Object createUserReport(Request request, Response response){
        Optional<User> reportedUser = getUserFromRequestById(request);
        String reportDescription = request.queryParams(Paths.QueryParams.REPORT_DESCRIPTION);
        User reporter = getUserFromSession(request);

        if (reportedUser.isPresent()) {
            if (reportedUser.get().equals(reporter)) {
                log.error("User can't report itself");
                return setStatusCodeAndReturnString(response, 400, USER_CANT_REPORT_SELF);
            } else {
                Optional<Report> userReport = ReportDAO.getInstance().createUserReport(reporter, reportedUser.get(), reportDescription);
                if (userReport.isPresent()) {
                    return setStatusCodeAndReturnString(response, 200, OK);
                }
            }
        }
        return setStatusCodeAndReturnString(response, 400, ERROR);
    }
}
