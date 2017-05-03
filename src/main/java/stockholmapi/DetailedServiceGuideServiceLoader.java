package stockholmapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.DetailedServiceUnit;
import stockholmapi.jsontojava.ServiceUnitTypes;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static stockholmapi.helpers.APIUtils.getUrl;
import static stockholmapi.helpers.APIUtils.stupidStockholmAPIJSONToNotStupidJSON;

@Slf4j
public class DetailedServiceGuideServiceLoader {

    private String apiKey;

    public DetailedServiceGuideServiceLoader(String apiKey) {
        this.apiKey = apiKey;
    }

    public static void main(String[] args) throws Exception {
        new DetailedServiceGuideServiceLoader("a42963ca81a64e55869481b281ad36c0").load(
                new ServiceGuideServiceLoader("a42963ca81a64e55869481b281ad36c0").load()
                , APIUtils.URLS.LEKPLATSER
        ).forEach(s -> System.out.println(s.getName()));

    }

    public List<DetailedServiceUnit> load(ServiceUnitTypes[] serviceUnitTypes, String serviceUnitTypeId) throws Exception {
        final int idsPerUrl = 20;
        final String baseUrl = APIUtils.URLS.urlHelper(APIUtils.URLS.MULTI_SERVICE_GUIDE_SERVICE_DETAILED_WITH_ID_PLACEHOLDER, serviceUnitTypeId, apiKey).toExternalForm();
        List<DetailedServiceUnit> detailedServiceUnits = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        for(int i = 0; i < serviceUnitTypes.length; i += idsPerUrl) {
            StringBuilder stringBuilder = new StringBuilder();
            for(int j = 0; j < idsPerUrl && i+j < serviceUnitTypes.length; j++) {
                stringBuilder.append(serviceUnitTypes[i + j].getId());
                if (j < idsPerUrl - 1 && i + j != serviceUnitTypes.length - 1) {
                    stringBuilder.append(",");
                }
            }
            urls.add(baseUrl.replace("{#ids#}", stringBuilder.toString()));
        }
        log.info("Starting loading detailed service info");
        long start = System.currentTimeMillis();
        for (String urlStr : urls) {
            String json = stupidStockholmAPIJSONToNotStupidJSON(getUrl(new URL(urlStr)));
            detailedServiceUnits.addAll(Arrays.asList(new ObjectMapper().readValue(json, DetailedServiceUnit[].class)));
        }
        log.info("loading and mapping info took: " + (System.currentTimeMillis() - start) + "ms");
        return detailedServiceUnits;
    }
}
