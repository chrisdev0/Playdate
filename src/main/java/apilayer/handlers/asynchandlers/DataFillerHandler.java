package apilayer.handlers.asynchandlers;

import model.User;
import spark.Request;
import spark.Response;

import static apilayer.handlers.asynchandlers.SparkHelper.*;

public class DataFillerHandler {


    public static Object injectData(Request request, Response response) {
        User user = getUserFromSession(request);














    }


}
