package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;

@Entity
public class Playdate {

    @Id
    private int id;

    private long startTime;
    private long endTime;

    private Playground playground;

    private PlaydateVisibilityType playdateVisibilityType;
    private User owner;
    private Set<User> participants;



}
