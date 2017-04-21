package model;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;

@Entity
public class Friendship {

    @Id
    private Long id;

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    @Type(type = "timestamp")
    private Date createdAt;


}
