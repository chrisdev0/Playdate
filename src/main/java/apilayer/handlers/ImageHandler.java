package apilayer.handlers;

import cache.Cache;
import lombok.extern.slf4j.Slf4j;
import model.DBAPIImage;
import spark.Request;
import spark.Response;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ImageHandler {

    public static Object handle(Request request, Response response) {
        String key = request.params("id");
        if (key == null || key.isEmpty() || key.equals("-1")) {
            response.redirect("/images/testlekplats.png");
            return "";
        }
        log.info("api image key = " + key);
        Cache cache = Cache.getInstance();
        try {
            DBAPIImage dbImage = cache.getDbImage(key);
            HttpServletResponse raw = response.raw();
            ServletOutputStream outputStream = raw.getOutputStream();
            outputStream.write(dbImage.getImageAsByte());
            outputStream.flush();
            outputStream.close();
            return response.raw();
        } catch (Exception e) {
            log.error("unable to load image");
        }
        return "error";
    }
}
