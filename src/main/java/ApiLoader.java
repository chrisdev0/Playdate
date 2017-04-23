
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import model.Place;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;

public class ApiLoader {

    public static void main(String[] args) throws Exception{
        final String API_KEY = "a42963ca81a64e55869481b281ad36c0";
        final String BASE_API_URL = "http://api.stockholm.se";
        String serviceUnits = "/ServiceGuideService/ServiceUnitTypes/9da341e4-bdc6-4b51-9563-e65ddc2f7434/ServiceUnits/json?apikey=";
        String url = BASE_API_URL + serviceUnits + API_KEY;
        String json = getUrl(url);
        Gson gson = new Gson();

        Obj[] obj = gson.fromJson(json, Obj[].class);
        Place place = new Place();
        Obj a = obj[0];
        place.setId(0L);
        place.setSthlmAPIid(a.Id);
        place.setGeoX(a.GeographicalPosition.X);
        place.setGeoY(a.GeographicalPosition.Y);
        place.setName(a.Name);
        System.out.println(place);
    }

    private class Obj {
        GeographicalPosition GeographicalPosition;
        String Id;
        String Name;
        String TimeCreated;
        String TimeUpdated;

        @Override
        public String toString() {
            return "Obj{" +
                    "geographicalPosition=" + GeographicalPosition +
                    ", Id='" + Id + '\'' +
                    ", Name='" + Name + '\'' +
                    ", TimeCreated=" + TimeCreated +
                    ", TimeUpdated=" + TimeUpdated +
                    '}';
        }
    }


    private class GeographicalPosition {
        int X, Y;

        @Override
        public String toString() {
            return "GeographicalPosition{" +
                    "X=" + X +
                    ", Y=" + Y +
                    '}';
        }
    }

    public static String getUrl(String urlStr) throws Exception{
        URL url = new URL(urlStr);
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        bufferedReader.close();
        return stringBuffer.toString();
    }

}
