package model;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@Data public class Comment implements Comparable<Comment>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    private Long id;

    @Column(nullable = false)
    @Expose
    private String comment;

    @ManyToOne
    @Expose
    private User commenter;

    private boolean hidden;

    @Column(columnDefinition="DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Expose
    private Date commentDate = new Date();


    public Comment(String comment, User commenter, boolean hidden) {
        this.comment = comment;
        this.commenter = commenter;
        this.hidden = hidden;
    }

    @Override
    public int compareTo(Comment o) {
        return o.commentDate.compareTo(commentDate);
    }
}
