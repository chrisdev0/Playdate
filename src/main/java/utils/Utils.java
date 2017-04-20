package utils;

public class Utils {


    /** Returnerar true om strängen som skickas in inte är
     *  null och inte är tom
     * */
    public static boolean isNotNullAndNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }

}
