package model;

import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class FriendshipRequest {

    @Id
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne(cascade = CascadeType.ALL)
    private User reciever;

    @Type(type = "timestamp")
    private Date createdAt;

}
