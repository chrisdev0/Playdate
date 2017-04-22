package stockholmAPItest;

import org.hibernate.annotations.Type;
import org.junit.BeforeClass;
import org.junit.Test;
import secrets.Secrets;
import secrets.SecretsParserException;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;

public class testGet {

    private static Secrets secrets;
    private static String apiKey = "";

    private String baseURL = "http://api.stockholm.se/ServiceGuideService/ServiceUnits/¤unitid¤/FileInfos?apikey=¤APIKEY¤";

    @BeforeClass
    public static void setUp() {
        try {
            secrets = Secrets.getInstance().loadSecretsFile("secrets.txt");
            Optional<String> optional = secrets.getValue("stockholmAPIKEY");
            assertNotNull(optional);
            apiKey = optional.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void downLoadAnImageFromRavinen() {
        String ravinenID = "134597ad-0ed7-47fc-b324-31686537d1b6";
        String url = baseURL.replace("¤unitid¤", ravinenID).replace("¤APIKEY", apiKey);
        System.out.println(url);



    }

}
