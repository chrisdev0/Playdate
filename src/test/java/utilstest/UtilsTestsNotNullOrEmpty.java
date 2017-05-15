package utilstest;

import org.junit.Test;
import utils.Utils;

import static org.junit.Assert.*;

public class UtilsTestsNotNullOrEmpty {

    @Test
    public void testNotNullOrEmpty() {
        String notNullOrEmpty = "textsträng";
        assertTrue(Utils.isNotNullAndNotEmpty(notNullOrEmpty));
    }

    @Test
    public void testIsNull() {
        assertFalse(Utils.isNotNullAndNotEmpty((String) null));
    }

    @Test
    public void testIsEmpty() {
        String isEmpty = "";
        assertFalse(Utils.isNotNullAndNotEmpty(isEmpty));
    }

    @Test
    public void testSpace() {
        String isSpace = " ";
        assertTrue(Utils.isNotNullAndNotEmpty(isSpace));
    }

}
