package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    private Long id;

    @Expose
    private String message;

    @ManyToOne
    @Expose
    private Playdate playdate;

    @ManyToOne
    @Expose
    private User invited;

    public Invite(String message, Playdate playdate, User invited) {
        this.message = message;
        this.playdate = playdate;
        this.invited = invited;
    }

    public Invite() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Invite invite = (Invite) o;

        if (id != null && invite.id != null) {
            return id.equals(invite.id);
        }

        if (id != null ? !id.equals(invite.id) : invite.id != null) return false;
        if (message != null ? !message.equals(invite.message) : invite.message != null) return false;
        if (!playdate.equals(invite.playdate)) return false;
        return invited.equals(invite.invited);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + playdate.hashCode();
        result = 31 * result + invited.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", playdate=" + playdate +
                ", invited=" + invited +
                '}';
    }
}
