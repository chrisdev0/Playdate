package model;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class FriendshipRequest {

    private Long id;

    private User sender;
    private User reciever;

    @Type(type = "timestamp")
    private Date createdAt;

}
