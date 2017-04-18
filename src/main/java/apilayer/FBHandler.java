package apilayer;

import com.google.gson.Gson;
import com.restfb.FacebookClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

public class FBHandler {

    private FBInfoRequestReceiver fbInfoRequestReceiver;

    public FBHandler() {
        fbInfoRequestReceiver = new FBInfoRequestReceiver();
    }

    public FBInfoRequestReceiver getFbInfoRequestReceiver() {
        return fbInfoRequestReceiver;
    }

    public class FBInfoRequestReceiver implements RequestHandler {

        Logger logger = LoggerFactory.getLogger(FBInfoRequestReceiver.class);

        @Override
        public Object handle(Request request, Response response) {
            String res = new Gson().toJson(request.body());

            for (String s : res.split("&")) {
                logger.info(s);
            }
            return "ok";
        }
    }


}
