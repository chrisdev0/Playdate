package model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
public class FriendshipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne(cascade = CascadeType.ALL)
    private User reciever;

    @Type(type = "timestamp")
    private Date createdAt;

}
