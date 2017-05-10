package model;

public enum PlaydateVisibilityType {

    PUBLIC(0),
    FRIENDS_ONLY(1),
    PRIVATE(2);

    private int nr;

    PlaydateVisibilityType(int nr) {
        this.nr = nr;
    }


    public int getNr() {
        return nr;
    }

    @SuppressWarnings("Duplicates")
    public static PlaydateVisibilityType intToPlaydateVisibilityType(int nr) {
        switch (nr) {
            case 0:
                return PUBLIC;
            case 1:
                return FRIENDS_ONLY;
            case 2:
                return PRIVATE;
        }
        throw new IllegalArgumentException("illegal playdate visibilitytype id");
    }

}
