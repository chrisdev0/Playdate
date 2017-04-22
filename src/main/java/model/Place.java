package model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sthlmAPIid;
    private String name;

    private String description;

    private String timeCreated;
    private String timeUpdated;



    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Comment> comments;

    private int geoX;
    private int geoY;

    public Place(String sthlmAPIid, String name, String description, String timeCreated, String timeUpdated, int geoX, int geoY) {
        this.sthlmAPIid = sthlmAPIid;
        this.name = name;
        this.description = description;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
        this.geoX = geoX;
        this.geoY = geoY;
        comments = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean addComment(Comment comment) {
        return comments.add(comment);
    }

    public boolean removeComment(Comment comment) {
        return comments.remove(comment);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Place() {
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place that = (Place) o;

        if (geoX != that.geoX) return false;
        if (geoY != that.geoY) return false;
        if (!sthlmAPIid.equals(that.sthlmAPIid)) return false;
        if (!name.equals(that.name)) return false;
        if (!timeCreated.equals(that.timeCreated)) return false;
        return timeUpdated.equals(that.timeUpdated);
    }

    @Override
    public int hashCode() {
        int result = sthlmAPIid.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + timeCreated.hashCode();
        result = 31 * result + timeUpdated.hashCode();
        result = 31 * result + geoX;
        result = 31 * result + geoY;
        return result;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", sthlmAPIid='" + sthlmAPIid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", timeCreated='" + timeCreated + '\'' +
                ", timeUpdated='" + timeUpdated + '\'' +
                ", comments=" + comments +
                ", geoX=" + geoX +
                ", geoY=" + geoY +
                '}';
    }

    public String getSthlmAPIid() {
        return sthlmAPIid;
    }

    public void setSthlmAPIid(String id) {
        this.sthlmAPIid = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(String timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public int getGeoX() {
        return geoX;
    }

    public void setGeoX(int geoX) {
        this.geoX = geoX;
    }

    public int getGeoY() {
        return geoY;
    }

    public void setGeoY(int geoY) {
        this.geoY = geoY;
    }
}
