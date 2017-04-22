package apilayer;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public interface RequestHandler {

    Object handle(Request request, Response response);

}
