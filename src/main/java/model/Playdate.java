package model;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Playdate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    private Long id;

    @Column(nullable = false)
    @Expose
    private String header;

    @Type(type = "text")
    @Expose
    private String description;

    @Expose
    private long startTime;

    @ManyToOne
    @Expose
    private User owner;

    @ManyToMany
    private Set<User> participants = new HashSet<>();

    @OneToMany
    private Set<Invite> invites = new HashSet<>();

    @OneToMany
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @Expose
    private Place place;

    @Expose
    private PlaydateVisibilityType playdateVisibilityType;

    public boolean addInvite(Invite invite) {
        return invites.add(invite);
    }

    public boolean removeInvite(Invite invite) {
        return invites.remove(invite);
    }

    public boolean addComment(Comment comment) { return comments.add(comment); }

    public boolean removeComment(Comment comment){ return comments.remove(comment); }

    public boolean addParticipant(User participant) {
        return participants.add(participant);
    }

    public boolean removeParticipant(User participant) {
        return participants.remove(participant);
    }

    public Playdate() {
    }

    public Playdate(String header, String description, long startTime, User owner, Place place, PlaydateVisibilityType playdateVisibilityType) {
        this.header = StringEscapeUtils.escapeHtml(header);
        this.description = StringEscapeUtils.escapeHtml(description);
        this.startTime = startTime;
        this.owner = owner;
        this.place = place;
        this.playdateVisibilityType = playdateVisibilityType;
    }

    public void setHeader(String header) {
        this.header = StringEscapeUtils.escapeHtml(header);
    }

    public void setDescription(String description) {
        this.description = StringEscapeUtils.escapeHtml(description);
    }

    public boolean userIsOwner(User user) {
        return owner.equals(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Playdate playdate = (Playdate) o;

        if (id != null ? !id.equals(playdate.id) : playdate.id != null) return false;
        if (startTime != playdate.startTime) return false;
        if (header != null ? !header.equals(playdate.header) : playdate.header != null) return false;
        if (description != null ? !description.equals(playdate.description) : playdate.description != null)
            return false;
        if (owner != null ? !owner.equals(playdate.owner) : playdate.owner != null) return false;
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
