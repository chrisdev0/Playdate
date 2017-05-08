package apilayer;

public class StaticFileTemplateHandlerImpl extends StaticFileTemplateHandler{

    public StaticFileTemplateHandlerImpl(String templateName, int onErrorHTTPStatusCode, boolean injectUser) throws IllegalArgumentException {
        super(templateName, onErrorHTTPStatusCode, injectUser);
    }

    public StaticFileTemplateHandlerImpl(String templateName, int onErrorHTTPStatusCode) throws IllegalArgumentException {
        super(templateName, onErrorHTTPStatusCode, false);
    }


}
