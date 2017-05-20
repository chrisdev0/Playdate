import apilayer.Constants;
import apilayer.WebServer;
import secrets.envvar.Secrets;

public class Starter {



    public static void main(String[] args) {
        Secrets.initSecrets(Constants.ENV_PRINT_DEBUG);
        new WebServer();
    }

}
