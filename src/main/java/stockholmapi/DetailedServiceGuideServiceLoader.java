package stockholmapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import stockholmapi.jsontojava.DetailedServiceUnit;
import stockholmapi.jsontojava.ServiceUnitTypes;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static stockholmapi.helpers.APIUtils.getUrl;
import static stockholmapi.helpers.APIUtils.stupidStockholmAPIJSONToNotStupidJSON;

public class DetailedServiceGuideServiceLoader {

    private String apiKey;

    public DetailedServiceGuideServiceLoader(String apiKey) {
        this.apiKey = apiKey;
    }

    public static void main(String[] args) throws Exception{
        new DetailedServiceGuideServiceLoader("a42963ca81a64e55869481b281ad36c0")
                .load(new ServiceGuideServiceLoader("a42963ca81a64e55869481b281ad36c0").load());
    }

    public void load(ServiceUnitTypes[] serviceUnitTypes) throws Exception {
        final int idsPerUrl = 20;
        final String baseUrl = "http://api.stockholm.se/ServiceGuideService/DetailedServiceUnits/json?ids={#ids#}&serviceunittypeid=9da341e4-bdc6-4b51-9563-e65ddc2f7434&apikey=a42963ca81a64e55869481b281ad36c0";
        List<DetailedServiceUnit> list = new ArrayList<>();
        System.out.println("Amount of sut = " + serviceUnitTypes.length);
        List<String> urls = new ArrayList<>();
        int count = 0;
        for(int i = 0; i < serviceUnitTypes.length; i += idsPerUrl) {
            StringBuilder stringBuilder = new StringBuilder();
            for(int j = 0; j < idsPerUrl && i+j < serviceUnitTypes.length; j++) {
                stringBuilder.append(serviceUnitTypes[i + j].getId());
                count++;
                if (j < idsPerUrl - 1 && i + j != serviceUnitTypes.length - 1) {
                    stringBuilder.append(",");
                }
            }
            String replacement = stringBuilder.toString();
            String replace = baseUrl.replace("{#ids#}", replacement);
            urls.add(replace);
        }
        System.out.println("urls added to list = " + count);
        System.out.println("amount of urls in list = " + urls.size());
        System.out.println("Starting loading detailed service info");
        long start = System.currentTimeMillis();
        for (String urlStr : urls) {
            String json = stupidStockholmAPIJSONToNotStupidJSON(getUrl(new URL(urlStr)));
            DetailedServiceUnit[] dsu = new ObjectMapper().readValue(json, DetailedServiceUnit[].class);
            list.addAll(Arrays.asList(dsu));
        }

        System.out.println("loading and mapping info took: " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("Created " + list.size() + " DetailedServiceUnits from response");
    }
}
