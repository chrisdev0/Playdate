package apilayer.handlers;

import lombok.extern.slf4j.Slf4j;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

@Slf4j
public class PlaceHandler {

    public static ModelAndView handleGetOnePlace(Request request, Response response) {
        String id = request.queryParams("placeId");
        try {
            long lId = Long.parseLong(id);
            log.info("fetching place with id = " + lId);
            return new GetOnePlaceHandler("showplacepage.vm", lId, 400)
                    .handleTemplateFileRequest(request, response);
        } catch (NullPointerException | NumberFormatException e) {
            log.info("client: " + request.ip() + " sent illegal place id = " + id + " e = " + e.getMessage());
            throw halt(400);
        }
    }

}
