package stockholmapi.temporaryobjects;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.sql.rowset.serial.SerialBlob;

@Entity
@Data
public class PlaceHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long internal_id;

    private String id;
    private long x, y;
    private String name;

    private String description;

    private SerialBlob image;

    private String zip;
    private String cityAddress;
    private String streetAddress;
    private String geoArea;
    private String category;

    public PlaceHolder(String id, long x, long y, String name, String description, SerialBlob image, String zip, String cityAddress, String streetAddress, String geoArea, String category) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
        this.description = description;
        this.image = image;
        this.zip = zip;
        this.cityAddress = cityAddress;
        this.streetAddress = streetAddress;
        this.geoArea = geoArea;
        this.category = category;
    }

    public PlaceHolder() {
    }
}
