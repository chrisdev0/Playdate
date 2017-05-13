package secrets;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

import static com.lexicalscope.jewel.cli.CliFactory.parseArgumentsUsingInstance;

@Slf4j
public class Loader {


    public static void injectArgsToSecretValues(String[] args) throws Exception {
        if (args.length == 0) {
            log.info("No args, trying to use secrets.txt");
            File file = new File("secrets.txt");
            if (file.exists()) {
                loadArgs(ApiKeys.fileToArgs(file));
            } else {
                log.error("Couldn't find secrets.txt");
                log.error("Couldn't ");
                System.exit(-3);
            }
        } else if (args.length == 1) {
            loadArgs(ApiKeys.fileToArgs(new File(args[0])));
        } else {
            loadArgs(args);
        }
    }

    private static void loadArgs(String[] args) {
        parseArgumentsUsingInstance(new ApiKeys(), args);
    }


}
