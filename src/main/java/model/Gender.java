package model;

public enum Gender {

    MALE(0, "Man"), FEMALE(1, "Kvinna"), OTHER(2, "Annat");

    private int id;
    private String frontname;

    Gender(int id, String frontname) {
        this.id = id;
        this.frontname = frontname;
    }

    public int getId() {
        return id;
    }

    public String getFrontname() {
        return frontname;
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
