package model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Playdate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String header;
    private String description;

    private long startTime;
    private long endTime;

    @ManyToOne(cascade = CascadeType.ALL)
    private User owner;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> participants;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private Place place;

    private PlaydateVisibilityType playdateVisibilityType;

    public Playdate() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public PlaydateVisibilityType getPlaydateVisibilityType() {
        return playdateVisibilityType;
    }

    public void setPlaydateVisibilityType(PlaydateVisibilityType playdateVisibilityType) {
        this.playdateVisibilityType = playdateVisibilityType;
    }
}
