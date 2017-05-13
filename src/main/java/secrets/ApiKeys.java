package secrets;

import com.lexicalscope.jewel.cli.Option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ApiKeys {

    private static String GOOGLE_MAPS_KEY = "";
    private static String STOCKHOLM_API_KEY = "";
    private static String DB_HOST = "";
    private static String DB_USER = "";
    private static String DB_PASSWORD = "";
    private static String FB_SALT = "";
    private static String FB_API_ID = "";
    private static String FB_SECRET = "";


    @Option(longName = "maps", shortName = "m")
    void setGoogleMapsKey(String v){
        GOOGLE_MAPS_KEY = v;
    }

    @Option(longName = "sthlm", shortName = "s")
    void setStockholmApiKey(String v) {
        STOCKHOLM_API_KEY = v;
    }

    @Option(longName = "dbhost", shortName = "dh")
    void setDBHost(String v) {
        DB_HOST = v;
    }

    @Option(longName = "dbuser", shortName = "du")
    void setDBUser(String v) {
        DB_USER = v;
    }

    @Option(longName = "dbpassword", shortName = "dp")
    void setDBPassword(String v) {
        DB_PASSWORD = v;
    }

    @Option(longName = "fbsalt", shortName = "fsa")
    void setFacebookSalt(String v) {
        FB_SALT = v;
    }

    @Option(longName = "fbappid", shortName = "fa")
    void setFacebookAPIId(String v) {
        FB_API_ID = v;
    }

    @Option(longName = "fbsecret", shortName = "fse")
    void setFacebookSecret(String v) {
        FB_SECRET = v;
    }

    public static String getGoogleMapsKey() {
        return GOOGLE_MAPS_KEY;
    }

    public static String getStockholmApiKey() {
        return STOCKHOLM_API_KEY;
    }

    public static String getDbHost() {
        return DB_HOST;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }

    public static String getFbSalt() {
        return FB_SALT;
    }

    public static String getFbApiId() {
        return FB_API_ID;
    }

    public static String getFbSecret() {
        return FB_SECRET;
    }

    public static String[] fileToArgs(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder buffer = new StringBuilder();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("--")) {
                buffer.append(line).append(" ");
            }
        }
        String content = buffer.toString().replace("\n", " ");
        return content.split(" ");
    }

    public static void printAllFields() {
        System.out.println("dbhost" + ApiKeys.getDbHost());
        System.out.println("dbuser" + ApiKeys.getDbUser());
        System.out.println("dbpassword" + ApiKeys.getDbPassword());
        System.out.println("fbsalt" + ApiKeys.getFbSalt());
        System.out.println("fbApiid" + ApiKeys.getFbApiId());
        System.out.println("fbsecret" + ApiKeys.getFbSecret());
        System.out.println("stockholm api key" + ApiKeys.getStockholmApiKey());
        System.out.println("google maps" + ApiKeys.getGoogleMapsKey());
    }
}
