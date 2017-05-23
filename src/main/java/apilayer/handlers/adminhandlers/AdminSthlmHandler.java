package apilayer.handlers.adminhandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import dblayer.PlaceDAO;
import dblayer.admin.AdminSthlmDao;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdminSthlmHandler extends StaticFileTemplateHandlerImpl {
    public AdminSthlmHandler() throws IllegalArgumentException {
        super("/admin/adminSthlm.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        map.put("loadedApis", PlaceDAO.getInstance().getLoadedAPIs());
        return Optional.of(map);
    }
}
