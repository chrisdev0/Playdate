package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Invite {

    @Id
    private Long id;

    private String message;

    private boolean declined;

    @ManyToOne
    private Playdate playdate;

    @ManyToOne
    private User invited;

    @ManyToOne
    private User inviter;

    public Invite(String message, boolean declined, Playdate playdate, User invited, User inviter) {
        this.message = message;
        this.declined = declined;
        this.playdate = playdate;
        this.invited = invited;
        this.inviter = inviter;
    }

    public Invite() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined(boolean declined) {
        this.declined = declined;
    }

    public Playdate getPlaydate() {
        return playdate;
    }

    public void setPlaydate(Playdate playdate) {
        this.playdate = playdate;
    }

    public User getInvited() {
        return invited;
    }

    public void setInvited(User invited) {
        this.invited = invited;
    }

    public User getInviter() {
        return inviter;
    }

    public void setInviter(User inviter) {
        this.inviter = inviter;
    }
}
