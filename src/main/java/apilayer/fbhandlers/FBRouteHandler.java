package apilayer.fbhandlers;

import apilayer.Constants;
import apilayer.handlers.Paths;
import dblayer.HibernateUtil;
import dblayer.UserDAO;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.hibernate.Session;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.halt;

@Slf4j
public class FBRouteHandler {

    /** todo REFACTOR
     * */
    public static Object handleFacebookLogin(Request request, Response response) {
        Optional<FacebookProfile> facebookProfileOptional = FBHelpers.getFacebookProfile(request, response);
        if (!facebookProfileOptional.isPresent()) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", "" + 400);
            map.put("error_msg", "no facebook profile");
            return halt(400, new VelocityTemplateEngine().render(new ModelAndView(map, "error.vm")));
        }
        try (Session session = HibernateUtil.getInstance().openSession()) {
            FacebookProfile facebookProfile = facebookProfileOptional.get();
            User user = FBHelpers.buildUserFromFacebookProfile(facebookProfile);
            Optional<User> userOptional = UserDAO.getInstance().saveUserOnLogin(user);

            if (userOptional.isPresent()) {
                user = userOptional.get();
            }
            request.session(true).attribute(Constants.USER_SESSION_KEY, user);
        } catch (Exception e) {
            log.error("error session ", e);
        }
        String attribute = request.session().attribute(Constants.ONLOGINREDIRECT);
        if (attribute != null) {
            request.session().attribute(Constants.ONLOGINREDIRECT, null);
            response.redirect(attribute);
        } else {
            response.redirect(Paths.PROTECTED + Paths.LANDING);
        }
        return "";
    }
}
