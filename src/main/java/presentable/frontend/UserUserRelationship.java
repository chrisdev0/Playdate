package presentable.frontend;

public enum UserUserRelationship {

    USER_EQUAL_USER(0),
    USERS_ARE_FRIENDS(1),
    USER_RECIEVED_FRIEND_REQUEST_FROM_USER(2),
    USER_SENT_FRIEND_REQUEST_TO_USER(3),
    USER_NO_RELATIONSHIP(4);


    private int id;

    public int getId() {
        return this.id;
    }

    UserUserRelationship(int id) {
        this.id = id;
    }
}
