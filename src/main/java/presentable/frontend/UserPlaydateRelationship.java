package presentable.frontend;

public enum UserPlaydateRelationship {

    USER_IS_OWNER(0),
    USER_IS_ATTENDING(1),
    USER_IS_INVITED(2),
    USER_CAN_JOIN(3);


    private int nr;

    public int getNr() {
        return nr;
    }

    UserPlaydateRelationship(int nr) {
        this.nr = nr;
    }
}
