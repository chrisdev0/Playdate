package util;

import org.junit.Test;
import utils.Utils;

import static org.junit.Assert.*;

public class NumberTest {

    @Test
    public void testCorrect() {
        String correctNumber = "0738833388";
        assertTrue(Utils.isValidPhoneNumber(correctNumber));
    }

    @Test
    public void testTooShortNumber() {
        String correctNumber = "073883338";
        assertFalse(Utils.isValidPhoneNumber(correctNumber));
    }

    @Test
    public void testNumberWithDash() {
        String correctNumber = "073-8833388";
        correctNumber = Utils.removeNonDigitChars(correctNumber);
        assertTrue(Utils.isValidPhoneNumber(correctNumber));
    }
}
