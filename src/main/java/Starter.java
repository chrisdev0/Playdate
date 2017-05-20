import apilayer.Constants;
import apilayer.WebServer;
import lombok.extern.slf4j.Slf4j;
import secrets.envvar.Secrets;

@Slf4j
public class Starter {



    public static void main(String[] args) {
        try {
            Secrets.initSecrets(Constants.ENV_PRINT_DEBUG);
        } catch (Exception e) {
            log.error("missing environment variables, can't run server");
            System.exit(-3);
        }
        new WebServer();
    }

}
