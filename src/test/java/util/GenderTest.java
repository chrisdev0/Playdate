package util;

import model.Gender;
import org.junit.Test;

import static org.junit.Assert.*;

public class GenderTest {


    @Test
    public void testCorrectMale() {
        Gender gender = Gender.genderIdToGender(0);
        assertEquals(gender, Gender.MALE);
    }

    @Test
    public void testCorrectFemale() {
        Gender gender = Gender.genderIdToGender(1);
        assertEquals(gender, Gender.FEMALE);
    }

    @Test
    public void testCorrectOther() {
        Gender gender = Gender.genderIdToGender(2);
        assertEquals(gender, Gender.OTHER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectOther() {
        assertEquals(Gender.genderIdToGender(1020), Gender.OTHER);
    }

    @Test
    public void testCorrectOtherFacebookMale() {
        Gender gender = Gender.genderFromFacebookGender(org.pac4j.core.profile.Gender.MALE);
        assertEquals(gender, Gender.MALE);
    }

    @Test
    public void testCorrectOtherFacebookFemale() {
        Gender gender = Gender.genderFromFacebookGender(org.pac4j.core.profile.Gender.FEMALE);
        assertEquals(gender, Gender.FEMALE);
    }

    @Test
    public void testCorrectOtherFacebookOther() {
        Gender gender = Gender.genderFromFacebookGender(org.pac4j.core.profile.Gender.UNSPECIFIED);
        assertEquals(gender, Gender.OTHER);
    }






}
