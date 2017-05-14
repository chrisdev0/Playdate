package apilayer.fbhandlers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.Gender;
import model.User;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.pac4j.sparkjava.SparkWebContext;
import spark.Request;
import spark.Response;

import java.net.URI;
import java.util.Optional;

@Slf4j
public class FBHelpers {

    /** Bygger en User från informationen värdena i FacebookProfile-objektet
     * */
    public static User buildUserFromFacebookProfile(FacebookProfile facebookProfile) {
        User user = new User(facebookProfile.getDisplayName(), facebookProfile.getEmail());
        user.setFacebookThirdPartyID(facebookProfile.getThirdPartyId());
        user.setFbToken(facebookProfile.getAccessToken());
        user.setGender(Gender.genderFromFacebookGender(facebookProfile.getGender()));
        user.setProfilePictureUrl(getProfilePictureOfAccessToken(facebookProfile));
        user.setFacebookLinkUrl(facebookProfile.getAttribute("link", URI.class).toString());
        return user;
    }

    /** Försöker skapa en FacebookProfile ur request och response för facebook-login-route
     *  Wrappar FacebookProfile i en Optional
     * */
    public static Optional<FacebookProfile> getFacebookProfile(Request request, Response response) {
        SparkWebContext sparkWebContext = new SparkWebContext(request, response);
        ProfileManager<FacebookProfile> profileManager = new ProfileManager<>(sparkWebContext);
        return profileManager.get(true);
    }

    private static String getProfilePictureOfAccessToken(FacebookProfile facebookProfile) {
        FacebookClient client = new DefaultFacebookClient(facebookProfile.getAccessToken(), Version.LATEST);
        try {
            JsonObject json = client.fetchObject("me/picture", JsonObject.class, Parameter.with("redirect", "false"));
            return json.get("data").asObject().get("url").asString();
        } catch (Exception e) {
            log.error("error fetchobject ",e);
        }
        return null;
    }
}
