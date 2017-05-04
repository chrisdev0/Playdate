package model;

public enum Gender {

    MALE(0), FEMALE(1), OTHER(2);

    private int id;

    Gender(int id) {
        this.id = id;
    }

    @SuppressWarnings("Duplicates")
    public static Gender genderIdToGender(int id) {
        switch (id) {
            case 0:
                return MALE;
            case 1:
                return FEMALE;
            case 2:
                return OTHER;
        }
        throw new IllegalArgumentException("illegal gender id");
    }

    public static Gender genderFromFacebookGender(org.pac4j.core.profile.Gender gender) {
        switch (gender) {
            case MALE:
                return MALE;
            case FEMALE:
                return FEMALE;
            default:
                return OTHER;
        }
    }
}
