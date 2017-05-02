package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
public class GeographicalArea {

    @Id
    private Integer id;

    private String friendlyId;
    private Boolean isCityArea;
    private String name;

    public static GeographicalArea createFromPOJOGeoArea(stockholmapi.jsontojava.GeographicalArea geographicalArea) {
        return new GeographicalArea(geographicalArea.getId(), geographicalArea.getFriendlyId(), geographicalArea.getIsCityArea(), geographicalArea.getName());
    }

}
