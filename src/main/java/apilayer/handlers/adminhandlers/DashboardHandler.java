package apilayer.handlers.adminhandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import dblayer.admin.DashboardDAO;
import lombok.extern.slf4j.Slf4j;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class DashboardHandler extends StaticFileTemplateHandlerImpl {

    public DashboardHandler() {
        super("/admin/adminIndex.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(DashboardDAO.getInstance().getDashboardCounts());

        return Optional.of(map);
    }
}
