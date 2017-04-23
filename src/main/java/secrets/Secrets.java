package secrets;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

@Slf4j
public class Secrets {

    private static Secrets instance;
    private Map<String,String> keyValueMap;
    private SecretsLoader secretsLoader;
    private Set<File> loadedFiles;

    /** Skapar .txt-läsar-objektet
     *  och initierar nyckel-värdemappen
     * */
    private Secrets() {
        secretsLoader = new SecretsLoader();
        keyValueMap = new HashMap<>();
        loadedFiles = new HashSet<>();
    }


    /** Försöker ladda nyckel-värde från filen med filnamnet som skickas in som parameter
     *  returnerar this för method chaining
     * */
    public Secrets loadSecretsFile(String fileName) throws SecretsParserException, IOException {
        loadSecretsFile(new File(fileName));
        return this;
    }

    /** Returnerar en optional som omsluter det eventuella värde som finns i nyckel-värdemappen
     *  för nyckeln som skickas in som key
     * */
    public Optional<String> getValue(String key) {
        return Optional.ofNullable(keyValueMap.get(key));
    }

    /** Försöker ladda nyckel-värde från filen som skickas in som parameter
     *  returnerar this för method chaining
     * */
    public Secrets loadSecretsFile(File file) throws IOException, SecretsParserException {
        if (loadedFiles.contains(file)) {
            log.info("Tried to load already loaded file " + file.getName());
            return this;
        } else {
            secretsLoader.extractKeyValuesFromFile(file, this);
            loadedFiles.add(file);
            log.info("Loaded " + file.getName() + " secret values file");
        }
        return this;
    }

    /** Lägger till alla värden från mappen som skickas in som parameter i nyckel-värdemappen
     * */
    void addToMap(Map<String, String> tempMap) {
        keyValueMap.putAll(tempMap);
    }

    /** Singleton-pattern
     * */
    public static Secrets getInstance() {
        if (instance == null) {
            instance = new Secrets();
        }
        return instance;
    }

}
