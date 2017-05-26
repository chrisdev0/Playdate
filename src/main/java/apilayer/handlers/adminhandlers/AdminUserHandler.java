package apilayer.handlers.adminhandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import dblayer.admin.AdminDao;
import dblayer.admin.AdminPlaceDao;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdminUserHandler extends StaticFileTemplateHandlerImpl {

    public AdminUserHandler() {
        super("/admin/adminUsers.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        map.put("users", AdminDao.getInstance().getAllUsers());
        return Optional.of(map);
    }
}
