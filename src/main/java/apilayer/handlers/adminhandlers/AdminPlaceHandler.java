package apilayer.handlers.adminhandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import spark.Request;

import java.util.Map;
import java.util.Optional;

public class AdminPlaceHandler extends StaticFileTemplateHandlerImpl {

    public AdminPlaceHandler() {
        super("/admin/adminPlace.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        return super.createModelMap(request);
    }
}
