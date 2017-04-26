package model;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import utils.PasswordHandler;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.swing.text.PlainDocument;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;


    private String password;
    private String salt;

    private String description;

    private String phoneNumber;

    private String profilePictureUrl;

    @OneToMany(mappedBy = "invited", fetch = FetchType.EAGER)
    private Set<Invite> invitesToPlaydates = new HashSet<>();

    private Gender gender;

    @OneToMany(fetch = FetchType.EAGER)
    @Cascade({CascadeType.ALL})
    private Set<Child> children = new HashSet<>();

    public User() {

    }


    public boolean addInvite(Invite invite) {
        return invitesToPlaydates.add(invite);
    }

    public boolean removeInvite(Invite invite) {
        return invitesToPlaydates.remove(invite);
    }

    public boolean addChild(Child child) {
        return children.add(child);
    }

    public boolean removeChild(Child child) {
        return children.remove(child);
    }

    public User(String sthlmAPIID, String name, String email, String password, String phoneNumber, String profilePictureUrl, Gender gender) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Set<Child> getChildren() {
        return children;
    }

    public void setChildren(Set<Child> children) {
        this.children = children;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(user.phoneNumber) : user.phoneNumber != null) return false;
        if (profilePictureUrl != null ? !profilePictureUrl.equals(user.profilePictureUrl) : user.profilePictureUrl != null)
            return false;
        if (invitesToPlaydates != null ? !invitesToPlaydates.equals(user.invitesToPlaydates) : user.invitesToPlaydates != null)
            return false;
        if (gender != user.gender) return false;
        return children != null ? children.equals(user.children) : user.children == null;
    }



    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (profilePictureUrl != null ? profilePictureUrl.hashCode() : 0);;
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", invitesToPlaydates=" + invitesToPlaydates +
                ", gender=" + gender +
                ", children=" + children +
                '}';
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public static User createUserHelper(User userWithoutPassword, String password) {
        userWithoutPassword.setSalt(PasswordHandler.generateUserSalt());
        userWithoutPassword.setPassword(PasswordHandler.hashUserPwd(password, userWithoutPassword.getSalt()));
        return userWithoutPassword;
    }
}
