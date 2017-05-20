import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import secrets.Secrets;

import static org.junit.Assert.*;

@Slf4j
public class SecretsLoaderTest {

    @Test
    public void testSecrets() {
        try {
            Secrets.initSecrets(true);
            assertTrue(true);
        } catch (Exception e) {
            fail("failed loading environment vars");
        }
    }

}
