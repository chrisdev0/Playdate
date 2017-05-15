package utilstest;

import org.junit.Test;
import spark.HaltException;
import utils.ParserHelpers;
import utils.Utils;

import java.math.BigInteger;

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

    @Test
    public void testCorrectParseInt() {
        Integer integer = ParserHelpers.parseToInt("1");
        assertEquals(1, integer.intValue());
    }

    @Test
    public void testCorrectParseLong() {
        Long l = ParserHelpers.parseToLong("123");
        assertEquals(123L, l.longValue());
    }

    @Test
    public void testMaxParseLong() {
        Long l = ParserHelpers.parseToLong("" + Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, l.longValue());
    }

    @Test
    public void testMaxInt() {
        Integer i = ParserHelpers.parseToInt("" + Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, i.intValue());
    }

    @Test(expected = HaltException.class)
    public void testIncorrectParseInt() {
        ParserHelpers.parseToInt("ett");
    }

    @Test(expected = HaltException.class)
    public void testIncorrectParseInt1() {
        ParserHelpers.parseToInt("" + ((long)Integer.MAX_VALUE) + 1);
    }

    @Test(expected = HaltException.class)
    public void testIncorrectParseLong() {
        ParserHelpers.parseToLong("ett");
    }


    @Test(expected = HaltException.class)
    public void testIncorrectParseLong1() {
        BigInteger bigInteger = new BigInteger("" + Long.MAX_VALUE);
        bigInteger = bigInteger.add(new BigInteger("1"));
        ParserHelpers.parseToLong("" + bigInteger.toString());
    }



}

