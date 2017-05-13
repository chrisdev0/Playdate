package utilstest;

import org.junit.Test;
import secrets.ApiKeys;
import secrets.Loader;

import static org.junit.Assert.*;

public class CLITest {


    @Test
    public void testLoadFromArgs() throws Exception {
        String[] args = new String[]{
                "--dbhost", "dbhosttest",
                "--dbuser", "user",
                "--dbpassword", "password",
                "--sthlm", "sthlm",
                "--maps", "map",
                "--fbsalt", "saltsalt",
                "--fbappid", "id123",
                "--fbsecret", "secretsvalue"
        };
        Loader.injectArgsToSecretValues(args);
        assertEquals("dbhosttest",ApiKeys.getDbHost());
        assertEquals("user",ApiKeys.getDbUser());
        assertEquals("password",ApiKeys.getDbPassword());
        assertEquals("sthlm",ApiKeys.getStockholmApiKey());
        assertEquals("map",ApiKeys.getGoogleMapsKey());
        assertEquals("saltsalt",ApiKeys.getFbSalt());
        assertEquals("id123",ApiKeys.getFbApiId());
        assertEquals("secretsvalue",ApiKeys.getFbSecret());
    }

}
