package stockholmapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.ServiceUnitTypes;

import static stockholmapi.helpers.APIUtils.getUrl;
import static stockholmapi.helpers.APIUtils.stupidStockholmAPIJSONToNotStupidJSON;

public class ServiceGuideServiceLoader {

    private String apiKey;

    public ServiceGuideServiceLoader(String apiKey) {
        this.apiKey = apiKey;
    }

    public ServiceUnitTypes[] load() throws Exception {
        String json = stupidStockholmAPIJSONToNotStupidJSON(getUrl(APIUtils.URLS.urlHelper(APIUtils.URLS.BASIC_INFO_PLACEHOLDER, APIUtils.URLS.LEKPLATSER, apiKey)));
        return new ObjectMapper().readValue(json, ServiceUnitTypes[].class);
    }

}
