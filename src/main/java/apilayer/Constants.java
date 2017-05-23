package apilayer;

public class Constants {

    public static final boolean DEV = false;

    public static final boolean ENV_PRINT_DEBUG = true;

    public static final String HIBERNATE_DB_URL_SETTINGS = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    public static final String FACEBOOK_SCOPE = "public_profile,email";

    public static final int GRID_SEARCH_AREA_SIZE = 2000;

    public static final String USER_SESSION_KEY = "user";
    public static final String MAGIC_MISSING_IMAGE = "-1";


    public static final String PROFILE_PICTURE_UPLOAD_NAME = "profile_picture_file";

    public static final int QUICK_PLACE_SEARCH_LIMIT = 10;

    public static final String ONLOGINREDIRECT = "onloginredirect";
    
    public static final long COUNT_PLAYDATE_AS_FUTURE_CUTOFF = 1000 * 60 * 60 * 18; //18 timmar
    public static final long COUNT_PLAYDATE_AS_NEAR_CUTOFF = 1000 * 60 * 60 * 18; //18 timmar
    public static long COUNT_PLAYDATE_AS_RECENT = 1000 * 60 * 60 * 18;

    public static final int SEARCH_USER_OFFSET = 15;
    public static final Long TIMEDECIDER = 86400000L * 30;
    public static final int SHORTDESCMIN = 3;
    public static final int SHORTDESCMAX = 20;
    public static final int LONGDESCMIN = 10;
    public static final int LONGDESCMAX = 300;


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
        public static final String USER_CANT_REPORT_SELF = "user_try_report_self";
        public static final String VALIDATION_ERROR = "validation_error";

        public static final String OK = "";
        public static final String ALREADY_INVITED = "already_invited";

        public class ValidationErrors {

            public static final String ERROR_STARTTIME = "_starttime";
            public static final String ERROR_PLACE = "_place";
            public static final String ERROR_DESCRIPTION = "_description";
            public static final String ERROR_HEADER = "_header";
        }
    }
    
    public class ADMIN {
        public static final String RUNNER_ALREADY_RUNNING = "api_running";

        public class DASHBOARD {
            public static final String REPORT_COUNTS = "report_counts";
            public static final String USER_COUNTS = "user_counts";
            public static final String PLACE_COUNTS = "place_counts"; 
            public static final String PLAYDATE_COUNTS = "playdate_counts"; 
            
        }
        
    }
}
