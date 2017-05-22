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
        String phoneStr = Utils.removeNonDigitChars(request.queryParams("phoneinput"));
        String validateError = getValidationErrorMsg(phoneStr, description);
        if (!validateError.isEmpty()) {
            return setStatusCodeAndReturnString(response, 400, Constants.MSG.VALIDATION_ERROR + validateError);
        }
        int genderInt = ParserHelpers.parseToInt(genderSelectValue);
        Gender gender = Gender.genderIdToGender(genderInt);
        user.setGender(gender);
        user.setPhoneNumber(phoneStr);
        user.setDescription(description);
        if (UserDAO.getInstance().saveUpdatedUser(user)) {
            return setStatusCodeAndReturnString(response, 200, Paths.PROTECTED + Paths.EDITPROFILE);
        }
        return setStatusCodeAndReturnString(response, 400, Constants.MSG.ERROR);
    }

    private static String getValidationErrorMsg(String phone, String description) {
        String ret = "";
        if (!Utils.isValidPhoneNumber(phone)) {
            ret += "_phone";
        }
        if (!Utils.validateLengthOfString(Constants.LONGDESCMIN, Constants.LONGDESCMAX, description)) {
            ret += "_description";
        }
        return ret;
    }

}
