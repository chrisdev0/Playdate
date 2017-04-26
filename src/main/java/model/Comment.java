package model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "comments")
@Data public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String comment;

    @ManyToOne
    private User commenter;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private Place place;

    private boolean hidden;

    @Column(columnDefinition="DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date commentDate = new Date();

    public Comment() {

    }

    public Comment(String comment, User commenter, boolean hidden, Place place) {
        this.comment = comment;
        this.commenter = commenter;
        this.hidden = hidden;
        this.place = place;
    }

}
