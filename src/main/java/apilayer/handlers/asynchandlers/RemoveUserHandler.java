package apilayer.handlers.asynchandlers;

import model.User;
import spark.Request;
import spark.Response;

import static apilayer.handlers.asynchandlers.SparkHelper.getUserFromSession;

public class RemoveUserHandler {


    public static Object handleRemoveUser(Request request, Response response) {
        User user = getUserFromSession(request);




    }


}
