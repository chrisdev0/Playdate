package apilayer;

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

    public ModelAndView handleTemplateFileRequest(Request request, Response response) {
        Optional<Map<String, Object>> opt = createModelMap(request);
        if (opt.isPresent()) {
            if (injectUser) {
                injectUser(request, opt.get());
            }
            opt.get().put("Utils", Utils.class);
            return new ModelAndView(opt.get(), templateName);
        } else {
            throw halt(haltNumber);
        }
    }

    private void injectUser(Request request, Map<String, Object> map) {
        map.put(Constants.USER_SESSION_KEY, request.session().attribute(Constants.USER_SESSION_KEY));
    }

    public Optional<Map<String,Object>> createModelMap(Request request) {
        return Optional.of(new HashMap<>());
    }
}
