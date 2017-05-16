package utilstest;

import model.User;
import org.junit.Test;
import spark.*;
import testutils.MockTestHelpers;
import testutils.ModelCreators;
import utils.Utils;

import static org.junit.Assert.*;

public class ValidationTest extends MockTestHelpers{


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

    @Test
    public void testValidatePhoneNumber(){
       /*
        Prova telefonnummer som inte matchar reg-ex.
         */
    }

    @Test
    public void testValidateLengthOfString(){
        /*
        Prova längd på sträng, kan prova alla olika?
         */
    }
}
