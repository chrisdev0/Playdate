package apilayer.fbhandlers;

import apilayer.Constants;
import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.conf.ConfigurationBuilder;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.oauth.profile.facebook.FacebookProfile;

@Slf4j
public class FacebookProfileHandler {

    public FacebookProfileHandler(String appId, String appSecret, FacebookProfile facebookProfile) throws Exception {
        ConfigurationBuilder conf = new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthAppSecret(appSecret)
                .setOAuthAppId(appId)
                .setOAuthAccessToken(facebookProfile.getAccessToken())
                .setOAuthPermissions(Constants.FACEBOOK_SCOPE);
        Facebook facebook = new FacebookFactory(conf.build()).getInstance();
        log.info("facebook picture url= " + facebook.getPictureURL().toString());
        log.info("facebook id = " + facebook.getId());
        log.info("facebook third party id" + facebook.getMe());
    }

    public void init() {

    }

}
