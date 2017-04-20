package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sthlmAPIID;

    private String name;
    private String email;

    private String password;

    private String profilePictureUrl;

    //private Set<User> friends;

    public User() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSthlmAPIID() {
        return sthlmAPIID;
    }

    public void setSthlmAPIID(String sthlmAPIID) {
        this.sthlmAPIID = sthlmAPIID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public User(String sthlmAPIID, String name, String email, String password, String profilePictureUrl) {
        this.sthlmAPIID = sthlmAPIID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;
        if (!sthlmAPIID.equals(user.sthlmAPIID)) return false;
        if (!name.equals(user.name)) return false;
        if (!email.equals(user.email)) return false;
        if (!password.equals(user.password)) return false;
        return profilePictureUrl.equals(user.profilePictureUrl);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + sthlmAPIID.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + profilePictureUrl.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", sthlmAPIID='" + sthlmAPIID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                '}';
    }
}
