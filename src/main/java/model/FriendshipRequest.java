package model;

import org.hibernate.annotations.Type;
import org.hibernate.mapping.Join;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"sender_id", "receiver_id"}))
public class FriendshipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private User sender;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private User receiver;

    @Type(type = "timestamp")
    private Date createdAt;

}
