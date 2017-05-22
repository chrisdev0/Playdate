package apilayer.handlers.templateHandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import dblayer.PlaceDAO;
import lombok.extern.slf4j.Slf4j;
import model.Place;
import secrets.Secrets;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GetOnePlaceHandler extends StaticFileTemplateHandlerImpl {

    public GetOnePlaceHandler() {
        super("showplace.vm", 400, true);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();

        String s = request.queryParams("placeId");
        try {
            Long placeId = Long.parseLong(s);
            Optional<Place> placeById = PlaceDAO.getInstance().getPlaceById(placeId);
            placeById.ifPresent(place -> map.put("place", place));
        } catch (NumberFormatException e) {
            log.info("error reading placeid");
            return Optional.empty();
        }
        return Optional.of(map);
    }
}
