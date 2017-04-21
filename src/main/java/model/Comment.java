package model;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Comment {

    @Id
    private Long id;

    private String comment;

    @ManyToOne
    private User commenter;

    private boolean hidden;

    @Type(type = "date")
    private Date commentDate;

    @Type(type = "timestamp")
    private Date createdAt;

    @ManyToOne
    private Place place;



}
