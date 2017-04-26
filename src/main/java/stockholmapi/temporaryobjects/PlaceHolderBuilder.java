package stockholmapi.temporaryobjects;

import javax.sql.rowset.serial.SerialBlob;

public class PlaceHolderBuilder {
    private String id;
    private long x;
    private long y;
    private String name;
    private String description;
    private SerialBlob image;
    private String zip;
    private String cityAddress;
    private String geoArea;
    private String category;
    private String streetAddress;

    public PlaceHolderBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public PlaceHolderBuilder setX(long x) {
        this.x = x;
        return this;
    }

    public PlaceHolderBuilder setY(long y) {
        this.y = y;
        return this;
    }

    public PlaceHolderBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PlaceHolderBuilder setDescription(String description) {
        this.description = description;
        return this;
    }



    public PlaceHolderBuilder setImage(SerialBlob image) {
        this.image = image;
        return this;
    }

    public PlaceHolderBuilder setZip(String zip) {
        this.zip = zip;
        return this;
    }

    public PlaceHolderBuilder setCityAddress(String cityAddress) {
        this.cityAddress = cityAddress;
        return this;
    }

    public PlaceHolderBuilder setGeoArea(String geoArea) {
        this.geoArea = geoArea;
        return this;
    }

    public PlaceHolderBuilder setCategory(String category) {
        this.category = category;
        return this;
    }

    public PlaceHolder createPlaceHolder() {
        return new PlaceHolder(id, x, y, name, description, image, zip, cityAddress, geoArea, category, streetAddress);
    }

    public PlaceHolderBuilder setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
        return this;
    }
}