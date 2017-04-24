package apilayer;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.halt;

public abstract class StaticFileTemplateHandler  {


    private String templateName;
    private int haltNumber;

    public StaticFileTemplateHandler(String templateName, int onErrorHTTPStatusCode) throws IllegalArgumentException{
        if (templateName == null || templateName.isEmpty()) {
            throw new IllegalArgumentException("template name can't be null or empty");
        }
        this.templateName = templateName;
        this.haltNumber = onErrorHTTPStatusCode;
    }

    public ModelAndView handleTemplateFileRequest(Request request, Response response) {
        Optional<Map<String, Object>> opt = createModelMap();
        if (opt.isPresent()) {
            return new ModelAndView(opt.get(), templateName);
        } else {
            throw halt(haltNumber);
        }
    }

    public Optional<Map<String,Object>> createModelMap() {
        return Optional.of(new HashMap<>());
    }
}
