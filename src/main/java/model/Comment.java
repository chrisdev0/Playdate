package model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "comments")
public class Comment {

    @Id
    private Long id;

    private String comment;

    @ManyToOne(cascade = CascadeType.ALL)
    private User commenter;

    private boolean hidden;

    @Type(type = "date")
    private Date commentDate;

    @Type(type = "timestamp")
    private Date createdAt;

    @ManyToOne(cascade = CascadeType.ALL)
    private Place place;

    public Comment() {

    }

    public Comment(String comment, User commenter, boolean hidden, Date commentDate, Date createdAt, Place place) {
        this.comment = comment;
        this.commenter = commenter;
        this.hidden = hidden;
        this.commentDate = commentDate;
        this.createdAt = createdAt;
        this.place = place;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getCommenter() {
        return commenter;
    }

    public void setCommenter(User commenter) {
        this.commenter = commenter;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
