package apilayer.fbhandlers;

import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import secrets.Secrets;
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

    public static Object handleFacebookLogin(Request request, Response response) {
        Optional<FacebookProfile> facebookProfileOptional = FBHelpers.getFacebookProfile(request, response);
        if (!facebookProfileOptional.isPresent()) {
            log.error("no facebook profile from request");
            Map<String, Object> map = new HashMap<>();
            map.put("code", "" + 400);
            map.put("error_msg", "no facebook profile");
            return halt(400, new VelocityTemplateEngine().render(new ModelAndView(map, "error.vm")));
        }
        try (Session session = HibernateUtil.getInstance().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE upper(email) = :email", User.class);
            query.setParameter("email", facebookProfileOptional.get().getEmail().toUpperCase());
            Optional<User> userOptional = query.uniqueResultOptional();
            userOptional.ifPresent(user -> log.info("found user " + user));
            FacebookProfile facebookProfile = facebookProfileOptional.get();
            FBHelpers.buildUserFromFacebookProfile(facebookProfile);
            Secrets secrets = Secrets.getInstance();
            Optional<String> fbAppIdOpt = secrets.getValue("fbAppId");
            Optional<String> fbSaltOpt = secrets.getValue("fbSalt");
            Optional<String> fbSecretOpt = secrets.getValue("fbSecret");
            FacebookProfileHandler facebookProfileHandler = new FacebookProfileHandler(fbAppIdOpt.get(), fbSecretOpt.get(), facebookProfile);


            request.session(true).attribute("user", userOptional.orElse(new User(facebookProfile.getEmail(),facebookProfile.getDisplayName())));
        } catch (Exception e) {
            log.error("error session ", e);
        }
        response.redirect("/protected/landingpage");
        return "";
    }
}
