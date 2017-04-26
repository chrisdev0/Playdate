package stockholmapi.temporaryobjects;

import lombok.Data;

import javax.sql.rowset.serial.SerialBlob;

@Data
public class PlaceHolder {

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
}
