package model;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data public class Playdate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String header;

    @Type(type = "text")
    private String description;

    private long startTime;

    @ManyToOne
    private User owner;

    @ManyToMany
    private Set<User> participants = new HashSet<>();

    @OneToMany(mappedBy = "invited", cascade = CascadeType.REMOVE)
    private Set<Invite> invites = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private Place place;

    private PlaydateVisibilityType playdateVisibilityType;

    public boolean addInvite(Invite invite) {
        return invites.add(invite);
    }

    public boolean removeInvite(Invite invite) {
        return invites.remove(invite);
    }

    public boolean addParticipant(User participant) {
        return participants.add(participant);
    }

    public boolean removeParticipant(User participant) {
        return participants.remove(participant);
    }

    public Playdate() {
    }

    public Playdate(String header, String description, long startTime, User owner, Place place, PlaydateVisibilityType playdateVisibilityType) {
        this.header = header;
        this.description = description;
        this.startTime = startTime;
        this.owner = owner;
        this.place = place;
        this.playdateVisibilityType = playdateVisibilityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Playdate playdate = (Playdate) o;

        if (startTime != playdate.startTime) return false;
        if (id != null ? !id.equals(playdate.id) : playdate.id != null) return false;
        if (header != null ? !header.equals(playdate.header) : playdate.header != null) return false;
        if (description != null ? !description.equals(playdate.description) : playdate.description != null)
            return false;
        if (owner != null ? !owner.equals(playdate.owner) : playdate.owner != null) return false;
        if (participants != null ? !participants.equals(playdate.participants) : playdate.participants != null)
            return false;
        if (invites != null ? !invites.equals(playdate.invites) : playdate.invites != null) return false;
        if (place != null ? !place.equals(playdate.place) : playdate.place != null) return false;
        return playdateVisibilityType == playdate.playdateVisibilityType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (header != null ? header.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (place != null ? place.hashCode() : 0);
        result = 31 * result + (playdateVisibilityType != null ? playdateVisibilityType.hashCode() : 0);
        return result;
    }
}
