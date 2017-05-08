package utils;

import lombok.extern.slf4j.Slf4j;
import spark.HaltException;

import static spark.Spark.halt;

@Slf4j
public class ParserHelpers {
    public static Integer parseToInt(String str) throws HaltException {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            log.error("error parsing int = " + str);
            throw halt(400);
        }
    }

    public static Long parseToLong(String str) throws HaltException {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            log.error("error parsing long = " + str);
            throw halt(400);
        }
    }
}
