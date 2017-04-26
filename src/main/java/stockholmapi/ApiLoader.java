package stockholmapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import stockholmapi.jsontojava.Attribute;
import stockholmapi.jsontojava.DetailedServiceUnit;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApiLoader {

    public static void main(String[] args) throws Exception{
        final String API_KEY = "a42963ca81a64e55869481b281ad36c0";
        final String BASE_API_URL = "http://api.stockholm.se";
        String serviceUnits = "/ServiceGuideService/ServiceUnitTypes/9da341e4-bdc6-4b51-9563-e65ddc2f7434/ServiceUnits?apikey=";
        String url = BASE_API_URL + serviceUnits + API_KEY;
        //System.out.println(url);
        //String xml = getUrl(url);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        //Document document = documentBuilder.parse(new URL(url).openStream());
        //printDocument(document,System.out);
        String jsonURL = "http://api.stockholm.se/ServiceGuideService/DetailedServiceUnits/134597ad-0ed7-47fc-b324-31686537d1b6/json?apikey=a42963ca81a64e55869481b281ad36c0";

        String json = getUrl(jsonURL).replace("Value\":{","Value2\":{");
        System.out.println(json);

        ObjectMapper objectMapper = new ObjectMapper();

        DetailedServiceUnit detailedServiceUnit = objectMapper.readValue(json, DetailedServiceUnit.class);
/*
        System.out.println(detailedServiceUnit.toString());
        for (int i = 0; i < detailedServiceUnit.getAttributes().size(); i++) {
            System.out.println("index " + i + " " + detailedServiceUnit.getAttributes().get(i));
        }*/
        System.out.println("printing values");
        String fileID = detailedServiceUnit.getAttributes().get(3).getValue2().getId();
        String fileUrl = "http://api.stockholm.se/ServiceGuideService/ImageFiles/{ID}/Data?apikey=" + API_KEY;
        fileUrl = fileUrl.replace("{ID}", fileID);
        URL fileURL = new URL(fileUrl);
        try (InputStream is = fileURL.openStream()) {
            Files.copy(is, Paths.get("D:/image.png"));
        }
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
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

    class ServiceUnitTypes {
        String Name, TimeUpdated, TimeCreated, Id;
        GeographicalPosition GeographicalPosition;

        @Override
        public String toString() {
            return "ServiceUnitTypes{" +
                    "Name='" + Name + '\'' +
                    ", Timeupdated='" + TimeUpdated + '\'' +
                    ", TimeCreated='" + TimeCreated + '\'' +
                    ", Id='" + Id + '\'' +
                    ", GeographicalPosition=" + GeographicalPosition +
                    '}';
        }
    }

    class GeographicalPosition {
        long X, Y;

        @Override
        public String toString() {
            return "GeographicalPosition{" +
                    "X=" + X +
                    ", Y=" + Y +
                    '}';
        }
    }



}
