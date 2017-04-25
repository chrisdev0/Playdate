package utils;

import spark.HaltException;

import static spark.Spark.halt;

public class ParserHelpers {
    public static Integer parseToInt(String str) throws HaltException {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw halt(400);
        }
    }

    public static Long parseToLong(String str) throws HaltException {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw halt(400);
        }
    }
}
