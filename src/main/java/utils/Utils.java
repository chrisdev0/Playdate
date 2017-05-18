package utils;

import apilayer.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    /** Returnerar true om strängen som skickas in inte är
     *  null och inte är tom
     * */
    public static boolean isNotNullAndNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }


    public static boolean isValidPhoneNumber(String number) {
        Pattern pattern = Pattern.compile("^07([0-9][ -]*){7}[0-9]$");
        Matcher matcher = pattern.matcher(number);
        return matcher.find();
    }

    public static String removeNonDigitChars(String string) {
        return string.replaceAll("\\D+", "");
    }


    public static boolean isNotNullAndNotEmpty(String... strings) {
        if (strings == null) {
            return false;
        }
        for (String string : strings) {
            if (string == null || string.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateLengthOfString(int min, int max, String input){
        input = input.trim();
        return !(input.length() < min || input.length() > max || input.equals(null));
    }

    public static boolean validateStartTime(Long startTime){
        return (startTime > System.currentTimeMillis() && startTime < (System.currentTimeMillis() + Constants.TIMEDECIDER));
    }

}
