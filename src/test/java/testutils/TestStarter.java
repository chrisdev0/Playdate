package testutils;

import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import secrets.envvar.Secrets;

@Slf4j
public class TestStarter {

    private static boolean isInit = false;
    private static boolean failedInit = true;

    static {
        try {
            if (!isInit) {
                Secrets.initSecrets(true);
                failedInit = false;
                isInit = true;
            }
        } catch (Exception e) {
            log.info("failed to init env vars");
        }
    }

    @BeforeClass
    public static void checkInitialized() {
        if (failedInit) {
            org.junit.Assert.fail("init of env vars failed");
        }
    }

}
