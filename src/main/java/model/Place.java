package model;

import com.sun.corba.se.spi.activation._InitialNameServiceImplBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import stockholmapi.helpers.APIUtils;
import stockholmapi.jsontojava.DetailedServiceUnit;
import stockholmapi.jsontojava.ServiceUnitType;
import stockholmapi.jsontojava.ServiceUnitTypes;
import stockholmapi.temporaryobjects.PlaceHolder;
import utils.CoordinateHandlerUtil;
import utils.Utils;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static stockholmapi.helpers.APIUtils.API_ZIP;

@Entity
@ToString
@NoArgsConstructor
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
        isInitialized = true;
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


    public boolean isInitialized() {
        return isInitialized;
    }

    public Place injectInfo(DetailedServiceUnit detailedServiceUnit) {
        setCategory(detailedServiceUnit.getServiceUnitTypes().get(0).getPluralName());
        setCityAddress((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_POST_ADDRESS));
        setGeoArea(detailedServiceUnit.getGeographicalAreas().get(0).getFriendlyId());
        setShortDescription((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_SHORT_DESC));
        setZip((String) detailedServiceUnit.getAttributesIdToValue().get(API_ZIP));
        setStreetAddress((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_STREET_ADDRESS));
        isInitialized = true;
        return this;
    }

    public Place(ServiceUnitTypes serviceUnitTypes) {
        name = serviceUnitTypes.getName();
        sthlmAPIid = serviceUnitTypes.getId();
        geoX = serviceUnitTypes.getGeographicalPosition().getX();
        geoY = serviceUnitTypes.getGeographicalPosition().getY();
        timeCreated = serviceUnitTypes.getTimeCreated();
        timeUpdated = serviceUnitTypes.getTimeUpdated();
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
}
