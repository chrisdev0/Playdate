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

    /** Skapar en URL med hjälp av urlHelper() och en mängd konstanter som finns i APIUtils.URLS
     *  Hämtar informationen på den URL-en med getUrl()
     *  Konverterar resultatet som innehåller fel som gör att JSON-resultatet inte kan konverteras till POJO
     *  med hjälp av stupidStockholmAPIJSONToNotStupidJSON() (som replacear namnet på de attribut som är okompatibla)
     * */
    public ServiceUnitTypes[] load(String apiType) throws Exception {
        String json = stupidStockholmAPIJSONToNotStupidJSON(getUrl(APIUtils.URLS.urlHelper(APIUtils.URLS.BASIC_INFO_PLACEHOLDER, apiType, apiKey)));
        return new ObjectMapper().readValue(json, ServiceUnitTypes[].class);
    }

}
