package model;

import lombok.Data;
import org.hibernate.annotations.Type;
import stockholmapi.temporaryobjects.PlaceHolder;
import utils.CoordinateHandlerUtil;
import utils.Utils;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String sthlmAPIid;

    private String name;

    @Type(type = "text")
    private String shortDescription;

    private String category;

    private String geoArea, cityAddress, zip, streetAddress;

    private String imageId;

    private String timeCreated;
    private String timeUpdated;

    private boolean isInitialized = false;

    public Place setInfo(PlaceHolder placeHolder) {
        //todo placeholderinfo -> place
        isInitialized = true;
        return this;
    }


    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Comment> comments = new HashSet<>();

    private int geoX;
    private int geoY;

    public Place(String sthlmAPIid, String name, String imageId, String timeCreated, String timeUpdated, int geoX, int geoY, String shortDescription) {
        this.sthlmAPIid = sthlmAPIid;
        this.name = name;
        this.imageId = imageId;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
        this.geoX = geoX;
        this.geoY = geoY;
        this.comments = new HashSet<>();
        this.shortDescription = shortDescription;
    }

    @Transient
    public double[] getGeoLonLat() {
        return new CoordinateHandlerUtil().gridToGeodetic(geoX, geoY);
    }

    @Transient
    public String getGeoLonLatStr() {
        return Arrays.toString(getGeoLonLat());
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

    public boolean isInitialized() {
        return shortDescription != null && !shortDescription.isEmpty();
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
                ", timeCreated='" + timeCreated + '\'' +
                ", timeUpdated='" + timeUpdated + '\'' +
                ", comments=" + comments +
                ", geoX=" + geoX +
                ", geoY=" + geoY +
                '}';
    }
}
