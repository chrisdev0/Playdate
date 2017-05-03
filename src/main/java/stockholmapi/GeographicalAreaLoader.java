package stockholmapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.GeographicalArea;

public class GeographicalAreaLoader {

    private String apiKey;

    public GeographicalAreaLoader(String apiKey) {
        this.apiKey = apiKey;
    }

    public GeographicalArea[] load() throws Exception {
        String json = APIUtils.getUrl(APIUtils.URLS.urlHelper(APIUtils.URLS.GEOGRAPHICAL_AREAS, apiKey));
        return new ObjectMapper().readValue(json, GeographicalArea[].class);
    }

}
