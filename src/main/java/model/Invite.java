package model;

import javax.persistence.*;

@Entity
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;

    private boolean declined;

    @ManyToOne
    private Playdate playdate;

    @ManyToOne
    private User invited;

    public Invite(String message, boolean declined, Playdate playdate, User invited) {
        this.message = message;
        this.declined = declined;
        this.playdate = playdate;
        this.invited = invited;
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

}
