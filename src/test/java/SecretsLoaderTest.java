import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import secrets.Secrets;
import secrets.SecretsParserException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@Slf4j
public class SecretsLoaderTest {

    @Test
    public void testSecrets() {
        try {
            secrets.envvar.Secrets.initSecrets(true);
            assertTrue(true);
        } catch (Exception e) {
            fail("failed loading environment vars");
        }
    }

}
