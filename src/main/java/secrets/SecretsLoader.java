package secrets;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class SecretsLoader {

    //Rader som börjar med denna symbol ses som kommentarer
    private static final String SECRETS_COMMENT_STRING = "#";

    /** Läser en fil med nyckel=värde-par och
     *  sparar dessa i en map i Secrets
     *
     *  Rader som börjar med # eller som är tomma laddas inte
     *  om filen innehåller något fel (exempelvis saknar ett likamed-tecken, eller saknar värde)
     *  så returneras ett SecretsParserException
     * */
    void extractKeyValuesFromFile(File file, Secrets secrets) throws IOException, SecretsParserException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        Map<String, String> tempMap = new HashMap<>();
        int lineNumber = 0;
        while ((line = bufferedReader.readLine()) != null) {
            log.info("line#" + lineNumber + ": " + line);
            lineNumber++;
            if (lineIsCommentOrEmpty(line)) {
                continue;
            }
            try {
                String[] extractedKeyValue = extractKeyValue(line);
                tempMap.put(extractedKeyValue[0], extractedKeyValue[1]);
            } catch (IllegalArgumentException e) {
                bufferedReader.close();
                throw new SecretsParserException("Error parsing line " + lineNumber + ". value = " + line);
            }
        }
        secrets.addToMap(tempMap);
        bufferedReader.close();
    }


    /**
     * Splittar och returnerar line på =
     * nyckeln får inte innehålla =
     * om värdet innehåller = så splittar den inte då 2 skickas in som limit
     * vilket gör att den endast splittar strängen på första = som finns
     *
     * Om det inte finns något = i raden så kastas ett nytt IllegalArgumentException
     */
    private String[] extractKeyValue(String line) throws IllegalArgumentException {
        String[] split = line.split("=", 2);
        if (split.length != 2 || split[0].isEmpty() || split[1].isEmpty()) {
            throw new IllegalArgumentException();
        }
        return split;
    }


    /** returnerar true om strängen som skickas in är tom eller börjar med "kommentartecknet"
     * */
    private boolean lineIsCommentOrEmpty(String line) {
        return line.startsWith(SECRETS_COMMENT_STRING) || line.trim().isEmpty();
    }
}
