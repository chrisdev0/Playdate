package stockholmapi;

import apilayer.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import secrets.Secrets;
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

    public DetailedServiceGuideServiceLoader() {
        this.apiKey = Secrets.STHML_API_KEY;
    }


    public List<DetailedServiceUnit> load(ServiceUnitTypes[] serviceUnitTypes, String serviceUnitTypeId) throws Exception {
        final int idsPerUrl = Constants.IDS_PER_URL;
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
        int runs = 0;
        for (String urlStr : urls) {
            String json = stupidStockholmAPIJSONToNotStupidJSON(getUrl(new URL(urlStr)));
            if (urls.size() > 10 && runs == 10) {
                runs = 0;
                log.info("sleeping loader thread for 10 seconds");
                Thread.sleep(10000);
            }
            detailedServiceUnits.addAll(Arrays.asList(new ObjectMapper().readValue(json, DetailedServiceUnit[].class)));
            runs++;
        }
        log.info("loading and mapping info took: " + (System.currentTimeMillis() - start) + "ms");
        return detailedServiceUnits;
    }
}
