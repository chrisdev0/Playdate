package apilayer.fbhandlers;

import apilayer.Constants;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;
import org.pac4j.sparkjava.SparkWebContext;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;

@Slf4j
public class FBConfigFactory implements ConfigFactory {

    private final String SALT;
    private final String APPID;
    private final String ABSOLUTE_CALLBACK;
    private final String SECRET;

    public FBConfigFactory(String salt, String appid, String absolute_callback, String secret) {
        SALT = salt;
        APPID = appid;
        ABSOLUTE_CALLBACK = Constants.ABSOLUTE_URL + absolute_callback;
        SECRET = secret;
    }

    @Override
    public Config build() {
        FacebookClient facebookClient = new FacebookClient(APPID, SECRET);
        facebookClient.setScope(Constants.FACEBOOK_SCOPE);
        Clients clients = new Clients(ABSOLUTE_CALLBACK, facebookClient);
        Config config = new Config(clients);
        config.setHttpActionAdapter(new CustomHttpActionAdapter());
        return config;
    }

    private class CustomHttpActionAdapter extends DefaultHttpActionAdapter {

        @Override
        public Object adapt(int code, SparkWebContext context) {
            if (code != 401 && code != 403) {
                log.info("context " + context.getPath() + " code = " + code);
                return super.adapt(code, context);
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("code", "" + code);
            map.put("error_msg", code == 401 ? "unathorized" : (code == 403 ? "forbidden" : "other reason"));
            return new VelocityTemplateEngine().render(new ModelAndView(map, "error.vm"));
        }
    }

}
