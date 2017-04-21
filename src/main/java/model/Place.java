package model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Place {

    @Id
    private String id;
    private String name;

    private String description;

    private String timeCreated;
    private String timeUpdated;

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Comment> comments;

    private int geoX;
    private int geoY;

    public Place(String name, String description, String timeCreated, String timeUpdated, int geoX, int geoY) {
        this.name = name;
        this.description = description;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
        this.geoX = geoX;
        this.geoY = geoY;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Place() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place that = (Place) o;

        if (geoX != that.geoX) return false;
        if (geoY != that.geoY) return false;
        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        if (!timeCreated.equals(that.timeCreated)) return false;
        return timeUpdated.equals(that.timeUpdated);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + timeCreated.hashCode();
        result = 31 * result + timeUpdated.hashCode();
        result = 31 * result + geoX;
        result = 31 * result + geoY;
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
