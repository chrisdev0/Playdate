package model;

import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Type(type = "text")
    private String description;

    private String phoneNumber;

    private String fbToken;

    @Column(unique = true)
    private String facebookThirdPartyID;

    private String profilePictureUrl;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.REMOVE)
    private Set<Friendship> friends = new HashSet<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    private Set<FriendshipRequest> friendshipRequest = new HashSet<>();

    @OneToMany(mappedBy = "invited", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Invite> invitesToPlaydates = new HashSet<>();

    @ManyToMany(mappedBy = "participants", fetch = FetchType.EAGER)
    private Set<Playdate> attendingPlaydates = new HashSet<>();

    private Gender gender;

    public User() {

    }

    public boolean attendPlaydate(Playdate playdate) {
        return attendingPlaydates.add(playdate);
    }

    public boolean removeAttendingPlaydate(Playdate playdate) {
        return attendingPlaydates.remove(playdate);
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email.toUpperCase();
    }

    public User(String email) {
        this.email = email;
    }

    public boolean addInvite(Invite invite) {
        return invitesToPlaydates.add(invite);
    }

    public boolean removeInvite(Invite invite) {
        return invitesToPlaydates.remove(invite);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        if ((id != null && user.getId() != null)){
            return id.equals(user.getId());
        }

        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(user.phoneNumber) : user.phoneNumber != null) return false;
        if (profilePictureUrl != null ? !profilePictureUrl.equals(user.profilePictureUrl) : user.profilePictureUrl != null)
            return false;
        if (invitesToPlaydates != null ? !invitesToPlaydates.equals(user.invitesToPlaydates) : user.invitesToPlaydates != null)
            return false;
        if (gender != user.gender) return false;
        else return true;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.toUpperCase() : email;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (profilePictureUrl != null ? profilePictureUrl.hashCode() : 0);;
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", invitesToPlaydates=" + invitesToPlaydates +
                ", gender=" + gender +
                //", children=" + children +
                '}';
    }
}
