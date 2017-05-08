package model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"friend_id", "requester_id"}))
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private User friend;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private User requester;

    @Column(columnDefinition="DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();


}
