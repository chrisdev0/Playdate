
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ApiLoader {

    public static void main(String[] args) throws Exception{
        final String API_KEY = "a42963ca81a64e55869481b281ad36c0";
        final String BASE_API_URL = "http://api.stockholm.se";
        String serviceUnits = "/ServiceGuideService/ServiceUnitTypes/9da341e4-bdc6-4b51-9563-e65ddc2f7434/ServiceUnits/?apikey=";
        String url = BASE_API_URL + serviceUnits + API_KEY;

    }


    public static String getUrl(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

}
