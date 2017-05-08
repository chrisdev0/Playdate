package model;

import apilayer.Constants;
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
import stockholmapi.jsontojava.Value2;
import stockholmapi.temporaryobjects.PlaceHolder;
import utils.CoordinateHandlerUtil;
import utils.Utils;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static stockholmapi.helpers.APIUtils.API_DESCRIPTION;
import static stockholmapi.helpers.APIUtils.API_HUVUDBILD;
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

    @Column(nullable = false)
    private String name;

    @Type(type = "text")
    private String shortDescription;

    @Type(type = "text")
    private String longDescription;

    private String category;

    private String geoArea, cityAddress, zip, streetAddress;

    private String imageId;

    private String timeCreated;
    private String timeUpdated;

    private boolean isInitialized = false;




    @OneToMany(cascade = CascadeType.REMOVE)
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
        setName(detailedServiceUnit.getName());
        setSthlmAPIid(detailedServiceUnit.getId());
        setCategory(detailedServiceUnit.getServiceUnitTypes().get(0).getPluralName());
        setCityAddress((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_POST_ADDRESS));
        setGeoArea(detailedServiceUnit.getGeographicalAreas().get(0).getFriendlyId());
        setShortDescription((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_SHORT_DESC));
        setZip((String) detailedServiceUnit.getAttributesIdToValue().get(API_ZIP));
        setStreetAddress((String) detailedServiceUnit.getAttributesIdToValue().get(APIUtils.API_STREET_ADDRESS));
        setGeoX(detailedServiceUnit.getGeographicalPosition().getX());
        setGeoY(detailedServiceUnit.getGeographicalPosition().getY());
        setLongDescription((String)detailedServiceUnit.getAttributesIdToValue().get(API_DESCRIPTION));
        isInitialized = true;
        return this;
    }

    /** todo kolla så att den hittar en bild om det är så att
     *  platsen har mer än 1 bild
     * */
    public static Place constructFromDetailedServiceUnit(DetailedServiceUnit detailedServiceUnit) {
        detailedServiceUnit.createMapOfAttributes();
        Place place = new Place().injectInfo(detailedServiceUnit);
        Object object = detailedServiceUnit.getAttributesIdToValue().get(API_HUVUDBILD);
        if (object != null && object instanceof Value2) {
            Value2 value2 = (Value2) object;
            place.setImageId(value2.getId());
        } else {
            place.setImageId(Constants.MAGIC_MISSING_IMAGE);
        }
        return place;
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
        if (!super.equals(o)) return false;

        Place place = (Place) o;

        if (isInitialized != place.isInitialized) return false;
        if (geoX != place.geoX) return false;
        if (geoY != place.geoY) return false;
        if (id != null ? !id.equals(place.id) : place.id != null) return false;
        if (sthlmAPIid != null ? !sthlmAPIid.equals(place.sthlmAPIid) : place.sthlmAPIid != null) return false;
        if (name != null ? !name.equals(place.name) : place.name != null) return false;
        if (shortDescription != null ? !shortDescription.equals(place.shortDescription) : place.shortDescription != null)
            return false;
        if (longDescription != null ? !longDescription.equals(place.longDescription) : place.longDescription != null)
            return false;
        if (category != null ? !category.equals(place.category) : place.category != null) return false;
        if (geoArea != null ? !geoArea.equals(place.geoArea) : place.geoArea != null) return false;
        if (cityAddress != null ? !cityAddress.equals(place.cityAddress) : place.cityAddress != null) return false;
        if (zip != null ? !zip.equals(place.zip) : place.zip != null) return false;
        if (streetAddress != null ? !streetAddress.equals(place.streetAddress) : place.streetAddress != null)
            return false;
        if (imageId != null ? !imageId.equals(place.imageId) : place.imageId != null) return false;
        if (timeCreated != null ? !timeCreated.equals(place.timeCreated) : place.timeCreated != null) return false;
        if (timeUpdated != null ? !timeUpdated.equals(place.timeUpdated) : place.timeUpdated != null) return false;
        return comments != null ? comments.equals(place.comments) : place.comments == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (sthlmAPIid != null ? sthlmAPIid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (shortDescription != null ? shortDescription.hashCode() : 0);
        result = 31 * result + (longDescription != null ? longDescription.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (geoArea != null ? geoArea.hashCode() : 0);
        result = 31 * result + (cityAddress != null ? cityAddress.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (streetAddress != null ? streetAddress.hashCode() : 0);
        result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
        result = 31 * result + (timeCreated != null ? timeCreated.hashCode() : 0);
        result = 31 * result + (timeUpdated != null ? timeUpdated.hashCode() : 0);
        result = 31 * result + (isInitialized ? 1 : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + geoX;
        result = 31 * result + geoY;
        return result;
    }
}
