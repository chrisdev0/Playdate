package apilayer.handlers.asynchandlers;

import model.User;
import spark.Request;
import spark.Response;

import static apilayer.handlers.asynchandlers.SparkHelper.*;

public class FeedHandler {

    public static Object handleGetFeed(Request request, Response response) {
        User user = getUserFromSession(request);
        return null;
    }

}
