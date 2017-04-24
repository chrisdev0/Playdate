package apilayer;

public class StaticFileTemplateHandlerImpl extends StaticFileTemplateHandler{

    public StaticFileTemplateHandlerImpl(String templateName, int onErrorHTTPStatusCode) throws IllegalArgumentException {
        super(templateName, onErrorHTTPStatusCode);
    }
}
