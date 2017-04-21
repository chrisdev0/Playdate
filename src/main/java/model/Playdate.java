package model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Playdate {

    @Id
    private int id;

    private String header;
    private String description;

    private long startTime;
    private long endTime;

    @ManyToOne
    private User owner;

    @ManyToMany
    private Set<User> participants;

    @ManyToOne
    private Place place;

    private PlaydateVisibilityType playdateVisibilityType;



}
