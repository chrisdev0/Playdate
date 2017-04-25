package apilayer;

public class Constants {

    public static final boolean DEV = true;

    public static final int PORT = 9000;


    public static final Object ON_LOGIN_SUCCESS_RETURN = "login_success";
    public static final Object ON_LOGIN_FAIL_RETURN = "login_failed";

    public static final String USER_SESSION_KEY = "user";
    public static final int LENGTH_OF_SALT = 20;

    public class MSG {

        public static final String USER_NOT_LOGGED_IN = "user_not_logged_in";
    }
}
