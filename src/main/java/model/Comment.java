package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@Data public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String comment;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private User commenter;

    /*
    @ManyToOne(cascade = CascadeType.ALL)
    private Place place;
*/
    private boolean hidden;

    @Column(columnDefinition="DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date commentDate = new Date();


    public Comment(String comment, User commenter, boolean hidden) {
        this.comment = comment;
        this.commenter = commenter;
        this.hidden = hidden;
    }
}
