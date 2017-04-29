package apilayer.route;

import apilayer.fbhandlers.FBConfigFactory;
import apilayer.handlers.CreateUserHandler;
import apilayer.handlers.LoginHandler;
import apilayer.handlers.Paths;
import cache.Cache;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.DBAPIImage;
import model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;
import secrets.Secrets;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static spark.Spark.*;
import static spark.Spark.halt;

@Slf4j
public class OpenRoutes {

    /** Initierar de routes som ska leda till statiska template-filer
     *  så att index.html (och "/") leder till att index.vm blir renderat
     * */
    public static void initOpenRoutes() {
        //Hanterar inloggningsförsök
        post(Paths.TRYLOGIN, new LoginHandler()::handleLoginTry);

        //hanterar registrering av användare
        post(Paths.DOREG, CreateUserHandler::handleCreateUser);

        //hanterar logout
        get(Paths.LOGOUT, LoginHandler::logOut);

        get(Paths.APIIMAGE + "/:id", (request, response) -> {
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
            } catch (ExecutionException e) {
                log.error("unable to load image");
            }
            return "error";
        });
        facebookLogin();
    }

    private static void facebookLogin() {
        Secrets secrets = Secrets.getInstance();
        Optional<String> fbAppIdOpt = secrets.getValue("fbAppId");
        Optional<String> fbSalt = secrets.getValue("fbSalt");
        Optional<String> fbSecret = secrets.getValue("fbSecret");
        if (!fbAppIdOpt.isPresent() && !fbSalt.isPresent() && !fbSecret.isPresent()) {
            log.error("missing facebook values");
            stop();
            return;
        }
        FBConfigFactory fbConfigFactory = new FBConfigFactory(fbSalt.get(), fbAppIdOpt.get(), Paths.FBCALLBACK, fbSecret.get());
        Config config = fbConfigFactory.build();
        SecurityFilter securityFilter = new SecurityFilter(config, "FacebookClient");
        before("/loginfb", securityFilter);
        get("/loginfb", (request, response) -> {
            StringBuilder stringBuilder = new StringBuilder();
            request.session().attributes().forEach(s -> {
                String line = "Session key " + s + " value = " + request.session().attribute(s);
                log.info(line);
                stringBuilder.append(line).append("<br>");
            });
            Optional<FacebookProfile> facebookProfileOptional = getFacebookProfile(request, response);
            facebookProfileOptional.ifPresent(facebookProfile -> log.info("Facebook profile is present with " + facebookProfile.getEmail()));
            if (!facebookProfileOptional.isPresent()) {
                log.error("no facebook profile from request");
                Map<String, Object> map = new HashMap<>();
                map.put("code", "" + 400);
                map.put("error_msg", "no facebook profile");
                return halt(400, new VelocityTemplateEngine().render(new ModelAndView(map, "error.vm")));
            }
            try (Session session = HibernateUtil.getInstance().openSession()) {
                Query<User> query = session.createQuery("FROM User WHERE upper(email) = :email", User.class);
                query.setParameter("email", facebookProfileOptional.get().getEmail());
                Optional<User> userOptional = query.uniqueResultOptional();
                userOptional.ifPresent(user -> log.info("found user " + user));
                FacebookProfile facebookProfile = facebookProfileOptional.get();
                request.session(true).attribute("user", userOptional.orElse(new User(facebookProfile.getEmail(),facebookProfile.getDisplayName())));
            } catch (Exception e) {
                log.error("error session ", e);
            }
            response.redirect("/protected/landingpage");
            return "";
        });

        CallbackRoute callbackRoute = new CallbackRoute(config);
        get(Paths.FBCALLBACK, callbackRoute);
        post(Paths.FBCALLBACK, callbackRoute);
    }

    private static Optional<FacebookProfile> getFacebookProfile(Request request, Response response) {
        SparkWebContext sparkWebContext = new SparkWebContext(request, response);
        ProfileManager<FacebookProfile> profileManager = new ProfileManager<>(sparkWebContext);
        return profileManager.get(true);
    }


}
