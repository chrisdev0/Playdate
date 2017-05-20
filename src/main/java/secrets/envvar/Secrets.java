package secrets.envvar;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class Secrets {

    public static String DB_HOST;
    public static String DB_USER;
    public static String DB_PASS;

    public static String STHML_API_KEY;
    public static String GOOGLE_MAPS_KEY;

    public static String FB_SALT;
    public static String FB_APP_ID;
    public static String FB_SECRET;
    public static String FB_CALLBACK;

    public static boolean USE_SSL;
    public static String KEYSTORE_PASSWORD;

    public static int PORT;


    Secrets() {
        Map<String, String> env = System.getenv();
        printEnv(env);
    }

    private void printEnv(Map<String, String> env) {
        env.forEach((s, s2) -> {
            log.info("[Key=" + s + "]=" + s2);
        });
    }

    public static void main(String[] args) {
        new Secrets();
    }


}
