package util;

import org.junit.Test;
import utils.Utils;

import static org.junit.Assert.*;

public class ValidationTest {


    @Test
    public void testMultiNotNullOrEmptyMultiNull() {
        assertFalse(Utils.isNotNullAndNotEmpty(null, null));
    }


    @Test
    public void testCorrect() {
        assertTrue(Utils.isNotNullAndNotEmpty("test", "test"));
    }


    @Test
    public void testCorrectAndNull() {
        assertFalse(Utils.isNotNullAndNotEmpty("test", null));
    }

    @Test
    public void testNullArray() {
        String[] n = null;
        assertFalse(Utils.isNotNullAndNotEmpty(n));
    }

}
