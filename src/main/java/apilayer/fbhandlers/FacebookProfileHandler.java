package apilayer.fbhandlers;


import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.oauth.profile.facebook.FacebookProfile;

@Slf4j
public class FacebookProfileHandler {

    public FacebookProfileHandler(String appId, String appSecret, FacebookProfile facebookProfile) throws Exception {
        FacebookClient client = new DefaultFacebookClient(facebookProfile.getAccessToken(), Version.LATEST);
        JsonObject json = null;
        try {
            json = client.fetchObject("me/picture", JsonObject.class, Parameter.with("redirect", "false"));
            log.info("image url = " + json.toString());
        } catch (Exception e) {
            log.error("error fetchobject ",e);
        }
    }

    public static String getProfilePictureOfAccessToken(FacebookProfile facebookProfile) {
        FacebookClient client = new DefaultFacebookClient(facebookProfile.getAccessToken(), Version.LATEST);
        JsonObject json = null;
        try {
            json = client.fetchObject("me/picture", JsonObject.class, Parameter.with("redirect", "false"));
            log.info("image url = " + json.toString());
            json.iterator().forEachRemaining(member -> log.info(member.toString()));
            log.info("data = " + json.get("data"));
            log.info("url = " + json.get("url"));
            log.info("picture = " + json.get("picture"));
            return json.get("data").asObject().get("url").asString();
        } catch (Exception e) {
            log.error("error fetchobject ",e);
        }
        return null;
    }

    public void init() {

    }

}
