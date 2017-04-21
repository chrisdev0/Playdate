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



}
