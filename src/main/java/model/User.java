package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;

@Entity
public class User {

    @Id
    private String id;

    private String name;
    private String email;

    private String profilePictureUrl;


    //private Set<User> friends;

}
