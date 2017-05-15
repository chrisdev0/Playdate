package apilayer.handlers.asynchandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.Gender;
import model.User;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;
import utils.Utils;
import static apilayer.handlers.asynchandlers.SparkHelper.*;

@Slf4j
public class ProfileHandlers {


    /** Hanterar ändra profilinformation
     *  Tar in
     *  description, gender och telefonnummer
     *  redirectar till samma sida så att den uppdaterade profilinformationen visas
     *  todo validera input
     * */
    public static Object handleEditProfile(Request request, Response response) {
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        String description = request.queryParams("description");

        String genderSelectValue = request.queryParams("genderselect");
        String phoneStr = request.queryParams("phoneinput");
        if (!Utils.isValidPhoneNumber(phoneStr) ||
                !Utils.validateLengthOfString(20, 300, description)){
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.VALIDATION_ERROR);
        }
        int genderInt = ParserHelpers.parseToInt(genderSelectValue);
        Gender gender = Gender.genderIdToGender(genderInt);
        user.setGender(gender);
        user.setPhoneNumber(phoneStr);
        user.setDescription(description);
        if (UserDAO.getInstance().saveUpdatedUser(user)) {
            response.redirect(Paths.PROTECTED + Paths.EDITPROFILE);
            return "";
        }
        response.status(400);
        return "";
    }

}
