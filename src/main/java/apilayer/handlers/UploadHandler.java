package apilayer.handlers;

import apilayer.Constants;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.ProfilePicture;
import model.User;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
public class UploadHandler {

    public static Object handleUploadProfilePicture(Request request, Response response) {
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        try {
            Part part = request.raw().getPart(Constants.PROFILE_PICTURE_UPLOAD_NAME);
            try (InputStream is = part.getInputStream()) {
                byte[] image = IOUtils.toByteArray(is);
                ProfilePicture profilePicture = new ProfilePicture();
                profilePicture.setImage(image);
                log.info("uploaded profile picture size = " + profilePicture.getImage().length);
                Optional<Long> aLong = UserDAO.getInstance().saveImageToDB(profilePicture);
                if (aLong.isPresent()) {
                    user.setProfilePictureUrl(Paths.PROTECTED + Paths.GETPROFILEPICTURE + "/" + aLong.get());
                    return user.getProfilePictureUrl();
                } else {
                    response.status(400);
                    return "";
                }
            }
        } catch (Exception e) {
            log.error("Couldn't upload image from " + user.getId(), e);
        }
        return "";
    }

}
