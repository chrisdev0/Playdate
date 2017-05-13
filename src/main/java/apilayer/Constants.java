package apilayer;

public class Constants {

    public static final boolean DEV = true;

    public static final int PORT = 9000;

    public static final String FACEBOOK_SCOPE = "public_profile,email";

    public static final int GRID_SEARCH_AREA_SIZE = 2000;

    public static final String USER_SESSION_KEY = "user";
    public static final int LENGTH_OF_SALT = 20;
    public static final String MAGIC_MISSING_IMAGE = "-1";
    public static final String ABSOLUTE_URL;

    public static final String PROFILE_PICTURE_UPLOAD_NAME = "profile_picture_file";
    public static final int QUICK_PLACE_SEARCH_LIMIT = 5;
    public static final String ONLOGINREDIRECT = "onloginredirect";

    static {
        if (DEV) {
            ABSOLUTE_URL = "http://localhost:" + PORT;
        } else {
            ABSOLUTE_URL = null;
        }
    }


    public class MSG {
        public static final String USER_NOT_LOGGED_IN = "user_not_logged_in";
        public static final String ON_LOGIN_SUCCESS_RETURN = "login_success";
        public static final String ON_LOGIN_FAIL_RETURN = "login_failed";
        public static final String NO_PLACE_WITH_ID = "no_place_with_id";
        public static final String NO_USER_WITH_ID = "no_user_with_id";
        public static final String NO_PLAYDATE_WITH_ID = "no_playdate_with_id";
        public static final String USER_IS_NOT_OWNER_OF_PLAYDATE = "user_is_not_owner_of_playdate";
        public static final String ERROR = "error";
        public static final String PLAYDATE_IS_NOT_PUBLIC = "playdate_is_not_public";
        public static final String USER_IS_NOT_OWNER_OF_INVITE = "user_is_not_owner_of_invite";
        public static final String NO_INVITE_WITH_ID = "no_invite_with_id";
        public static final String OK = "";
    }
}
