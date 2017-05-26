package apilayer;

import dblayer.UserDAO;
import model.User;
import secrets.Secrets;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.halt;

public abstract class StaticFileTemplateHandler  {


    private String templateName;
    private int haltNumber;
    private boolean injectUser;

    public StaticFileTemplateHandler(String templateName, int onErrorHTTPStatusCode, boolean injectUser) throws IllegalArgumentException{
        this.injectUser = injectUser;
        if (templateName == null || templateName.isEmpty()) {
            throw new IllegalArgumentException("template name can't be null or empty");
        }
        this.templateName = templateName;
        this.haltNumber = onErrorHTTPStatusCode;
    }


    public Map<String, Object> onErrorPage(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("error_msg", message);
        return map;
    }

    public ModelAndView handleTemplateFileRequest(Request request, Response response) {
        Optional<Map<String, Object>> opt = createModelMap(request);
        if (opt.isPresent()) {
            if (injectUser) {
                injectUser(request, opt.get());
            }
            opt.get().put("Utils", Utils.class);
            opt.get().put("mapsapikey", Secrets.GOOGLE_MAPS_KEY);
            return new ModelAndView(opt.get(), templateName);
        } else {
            return new ModelAndView(onErrorPage("Serverfel, försök igen senare"), "on-error-page-logged-in.vm");
        }
    }

    private void injectUser(Request request, Map<String, Object> map) {
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        map.put(Constants.USER_SESSION_KEY, user);
        map.put(Constants.USER_INVITE_COUNT, UserDAO.getInstance().getUserInviteCount(user));
        map.put(Constants.USER_FRIENDREQUEST_COUNT, UserDAO.getInstance().getUserFriendshipRequestCount(user));
    }

    public Optional<Map<String,Object>> createModelMap(Request request) {
        return Optional.of(new HashMap<>());
    }
}
