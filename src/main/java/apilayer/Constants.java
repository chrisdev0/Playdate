package apilayer;

public class Constants {

    public static final boolean DEV = true;

    public static final int PORT = 9000;


    public static final Object ON_LOGIN_SUCCESS_RETURN = "login_success";
    public static final Object ON_LOGIN_FAIL_RETURN = "login_failed";

    public static final String FACEBOOK_SCOPE = "public_profile,email";

    public static final int GRID_SEARCH_AREA_SIZE = 2000;

    public static final String USER_SESSION_KEY = "user";
    public static final int LENGTH_OF_SALT = 20;
    public static final String MAGIC_MISSING_IMAGE = "-1";
    public static final String ABSOLUTE_URL;

    public static final String PROFILE_PICTURE_UPLOAD_NAME = "profile_picture_file";

    static {
        if (DEV) {
            ABSOLUTE_URL = "http://192.168.1.67:" + PORT;
        } else {
            ABSOLUTE_URL = null;
        }
    }


    public class MSG {

        public static final String USER_NOT_LOGGED_IN = "user_not_logged_in";
    }
}
