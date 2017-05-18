package secrets;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

@Slf4j
public class Secrets implements GetSecretValue {

    private static Secrets instance;

    private Map<String,String> keyValueMap;
    private SecretsLoader secretsLoader;
    private Set<File> loadedFiles;

    public void injectValuesFromArgs(String[] args) throws Exception{
        if (args.length == 0) {
            loadSecretsFile("secrets.txt");
        } else if (args.length == 1) {
            File file = new File(args[0]);
            if (file.exists()) {
                loadSecretsFile(file);
            } else {
                log.error("Couldn't load secrets file " + args[0]);
                throw new Exception();
            }
        } else {
            if (args.length == 8) {

            }
        }
    }

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
    @Override
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
            instance.initSecrets();
        }
        return instance;
    }

    private void initSecrets() {
        File file = new File("secrets.txt");
        if (file.exists()) {
            try {
                loadSecretsFile(file);
            } catch (Exception e) {
                log.error("error loading secrets file, ");
            }
        }
    }

}
