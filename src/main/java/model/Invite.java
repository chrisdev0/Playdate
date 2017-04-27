package model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data public class Invite {

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

}
