package apilayer.handlers.asynchandlers;

import cache.Cache;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.DBAPIImage;
import model.ProfilePicture;
import org.apache.commons.lang.ArrayUtils;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ImageHandler {

    public static Object handleGetAPIImage(Request request, Response response) {
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

    public static Object handleGetProfilePicture(Request request, Response response) {
        String key = request.params("id");
        if (key == null || key.isEmpty() || key.equals("-1")) {
            response.redirect("/images/testprofile.jpg");
            return "";
        }
        try {
            Long imageId = ParserHelpers.parseToLong(key);
            Optional<ProfilePicture> profilePictureOfUser = UserDAO.getInstance().getProfilePictureOfUser(imageId);
            if (profilePictureOfUser.isPresent()) {
                HttpServletResponse raw = response.raw();
                ServletOutputStream outputStream = raw.getOutputStream();
                outputStream.write(ArrayUtils.toPrimitive(profilePictureOfUser.get().getImage()));
                outputStream.flush();
                outputStream.close();
                return response.raw();
            }
        } catch (Exception e) {
            log.error("error getting profile picture with id = " + key, e);
        }
        response.redirect("/images/testprofile.jpg");
        return "";
    }
}
