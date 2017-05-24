package secrets;

import lombok.extern.slf4j.Slf4j;


import java.util.Map;

@Slf4j
public class Secrets {

    public static String DB_HOST;
    public static String DB_USER;
    public static String DB_PASS;

    public static String STHML_API_KEY;
    public static String GOOGLE_MAPS_KEY;
    public static String TRAFIKLAB_KEY;

    public static String FB_SALT;
    public static String FB_APP_ID;
    public static String FB_SECRET;
    public static String FB_CALLBACK;

    public static Boolean USE_SSL;
    public static String KEYSTORE_PASSWORD;

    public static Integer PORT;

    private static class ENV_KEYS {

        public static String KEY_TO_DB_HOST = "PLAYDATE_DB_HOST";
        public static String KEY_TO_DB_USER = "PLAYDATE_DB_USER";
        public static String KEY_TO_DB_PASS = "PLAYDATE_DB_PASS";

        public static String KEY_TO_STHML_API_KEY = "PLAYDATE_STOCKHOLM_KEY";
        public static String KEY_TO_GOOGLE_MAPS_KEY = "PLAYDATE_GOOGLE_MAPS_KEY";

        public static String KEY_TO_FB_SALT = "PLAYDATE_FB_SALT";
        public static String KEY_TO_FB_APP_ID = "PLAYDATE_FB_APP_ID";
        public static String KEY_TO_FB_SECRET = "PLAYDATE_FB_SECRET";
        public static String KEY_TO_FB_CALLBACK = "PLAYDATE_CALLBACK";

        public static String KEY_TO_USE_SSL = "PLAYDATE_USE_SSL";
        public static String KEY_TO_KEYSTORE_PASSWORD = "PLAYDATE_KEYSTORE_PASS";

        public static String KEY_TO_PORT = "PLAYDATE_SERVER_PORT";

        public static String KEY_TO_TRAFIKLAB_KEY = "PLAYDATE_TRAFIKLAB_KEY";


    }


    public  static void initSecrets(boolean debug) throws Exception {
        Map<String, String> env = System.getenv();
        if (debug) {
            printEnv(env);
        }
        DB_HOST = env.get(ENV_KEYS.KEY_TO_DB_HOST);
        DB_USER = env.get(ENV_KEYS.KEY_TO_DB_USER);
        DB_PASS = env.get(ENV_KEYS.KEY_TO_DB_PASS);

        STHML_API_KEY = env.get(ENV_KEYS.KEY_TO_STHML_API_KEY);
        GOOGLE_MAPS_KEY = env.get(ENV_KEYS.KEY_TO_GOOGLE_MAPS_KEY);

        FB_APP_ID = env.get(ENV_KEYS.KEY_TO_FB_APP_ID);
        FB_CALLBACK = env.get(ENV_KEYS.KEY_TO_FB_CALLBACK);
        FB_SALT = env.get(ENV_KEYS.KEY_TO_FB_SALT);
        FB_SECRET = env.get(ENV_KEYS.KEY_TO_FB_SECRET);

        extractUseSSL(env.get(ENV_KEYS.KEY_TO_USE_SSL));
        KEYSTORE_PASSWORD = env.get(ENV_KEYS.KEY_TO_KEYSTORE_PASSWORD);

        TRAFIKLAB_KEY = env.get(ENV_KEYS.KEY_TO_TRAFIKLAB_KEY);

        extractPort(env.get(ENV_KEYS.KEY_TO_PORT));

        checkFields();
        log.info("All settings loaded successfully");
    }

    private static void extractPort(String portStr) throws Exception {
        try {
            PORT = Integer.parseInt(portStr);
        } catch (Exception e) {
            log.error("Illegal port number " + portStr);
            throw new Exception();
        }

    }

    private static void extractUseSSL(String ssl) throws Exception{
        if (ssl == null) {
            log.error("SSL enviroment variable not set, Please set the " + ENV_KEYS.KEY_TO_USE_SSL + " environment variable");
            throw new Exception();
        }
        ssl = ssl.toLowerCase();
        switch (ssl) {
            case "yes":
            case "true":
                USE_SSL = true;
                break;
            case "no":
            case "false":
                USE_SSL = false;
                break;
            default:
                log.error("incorrect USE_SSL value, must be yes true no or false");
                throw new Exception();
        }
    }

    private static void checkFields() throws Exception {
        if (("" + DB_USER + DB_HOST + DB_PASS +
                FB_CALLBACK + FB_APP_ID + FB_SECRET + FB_SALT +
                KEYSTORE_PASSWORD + PORT +
                USE_SSL + GOOGLE_MAPS_KEY + STHML_API_KEY).contains("null")) {
            log.error("unset variable");
            throw new Exception();
        }
    }

    private static void printEnv(Map<String, String> env) {
        log.info("Using the following environment variables");
        env.forEach((s, s2) -> {
            if (s.startsWith("PLAYDATE")) {
                log.info("[Key=" + s + "]=" + s2);
            }
        });
    }



}
