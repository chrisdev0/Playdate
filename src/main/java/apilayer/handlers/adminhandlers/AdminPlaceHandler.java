package apilayer.handlers.adminhandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import dblayer.admin.AdminPlaceDao;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdminPlaceHandler extends StaticFileTemplateHandlerImpl {

    public AdminPlaceHandler() {
        super("/admin/adminPlace.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        map.put("places", AdminPlaceDao.getInstance().getAllPlaces());
        return Optional.of(map);
    }
}
