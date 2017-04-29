package apilayer.fbhandlers;

import apilayer.route.OpenRoutes;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.pac4j.sparkjava.SparkWebContext;
import spark.Request;
import spark.Response;

import java.util.Optional;

@Slf4j
public class FBHelpers {

    /** todo
     *  Bygger en User från informationen värdena i FacebookProfile-objektet
     * */
    public static User buildUserFromFacebookProfile(FacebookProfile facebookProfile) {
        String email = facebookProfile.getEmail();
        String name = facebookProfile.getDisplayName();
        String fbToken = facebookProfile.getAccessToken();
        String id = facebookProfile.getId();
        String thirdPartyId = facebookProfile.getThirdPartyId();
        log.info("logging facebook info");
        facebookProfile.getAttributes().forEach((s, o) -> {
            log.info("[key=" + s + "]-[value=" + o + "]");
        });
        log.info("end facebook info");
        return null;
    }

    /** Försöker skapa en FacebookProfile ur request och response för facebook-login-route
     *  Wrappar FacebookProfile i en Optional
     * */
    public static Optional<FacebookProfile> getFacebookProfile(Request request, Response response) {
        SparkWebContext sparkWebContext = new SparkWebContext(request, response);
        ProfileManager<FacebookProfile> profileManager = new ProfileManager<>(sparkWebContext);
        return profileManager.get(true);
    }
}
