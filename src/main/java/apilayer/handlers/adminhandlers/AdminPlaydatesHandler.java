package apilayer.handlers.adminhandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import dblayer.admin.AdminDao;
import dblayer.admin.AdminPlaceDao;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdminPlaydatesHandler extends StaticFileTemplateHandlerImpl {

    public AdminPlaydatesHandler() {
        super("/admin/adminPlaydates.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        map.put("playdates", AdminDao.getInstance().getAllPlaydates());
        return Optional.of(map);
    }
}
