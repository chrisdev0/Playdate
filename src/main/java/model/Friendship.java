package model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Friendship {

    @Id
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user1;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user2;

    @Type(type = "timestamp")
    private Date createdAt;


}
