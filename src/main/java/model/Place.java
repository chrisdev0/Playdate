package model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sthlmAPIid;

    private String name;

    private String description;

    private String shortDescription;


    private String imageUrl;


    private String timeCreated;
    private String timeUpdated;


    @OneToMany(mappedBy = "place", cascade = CascadeType.REMOVE)
    private Set<Comment> comments = new HashSet<>();

    private int geoX;
    private int geoY;

    public Place(String sthlmAPIid, String name, String description, String imageUrl, String timeCreated, String timeUpdated, int geoX, int geoY, String shortDescription) {
        this.sthlmAPIid = sthlmAPIid;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
        this.geoX = geoX;
        this.geoY = geoY;
        this.comments = new HashSet<>();
        this.shortDescription = shortDescription;
    }

    public boolean addComment(Comment comment) {
        return comments.add(comment);
    }

    public boolean removeComment(Comment comment) {
        return comments.remove(comment);
    }

    public Place() {
        shortDescription = "";
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
}
