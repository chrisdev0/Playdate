package model;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Friendship {

    @Id
    private Long id;

    private User user1;
    private User user2;

    @Type(type = "timestamp")
    private Date createdAt;


}
