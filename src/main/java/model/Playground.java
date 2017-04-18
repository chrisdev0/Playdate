package model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Playground {

    @Id
    private String id;
    private String name;
    private String timeCreated;
    private String timeUpdated;

    private int geoX;
    private int geoY;

    public Playground(String id, String name, String timeCreated, String timeUpdated, int geoX, int geoY) {
        this.id = id;
        this.name = name;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
        this.geoX = geoX;
        this.geoY = geoY;
    }

    public Playground() {
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
