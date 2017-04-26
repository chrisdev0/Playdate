
package stockholmapi.jsontojava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Attributes",
    "GeographicalAreas",
    "GeographicalPosition",
    "Id",
    "Name",
    "RelatedServiceUnits",
    "ServiceUnitTypes",
    "TimeCreated",
    "TimeUpdated"
})
public class DetailedServiceUnit {

    @JsonProperty("Attributes")
    private List<Attribute> attributes = new ArrayList<Attribute>();
    @JsonProperty("GeographicalAreas")
    private List<GeographicalArea> geographicalAreas = new ArrayList<GeographicalArea>();
    @JsonProperty("GeographicalPosition")
    private GeographicalPosition geographicalPosition;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("RelatedServiceUnits")
    private List<Object> relatedServiceUnits = new ArrayList<Object>();
    @JsonProperty("ServiceUnitTypes")
    private List<ServiceUnitType> serviceUnitTypes = new ArrayList<ServiceUnitType>();
    @JsonProperty("TimeCreated")
    private String timeCreated;
    @JsonProperty("TimeUpdated")
    private String timeUpdated;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Attributes")
    public List<Attribute> getAttributes() {
        return attributes;
    }

    @JsonProperty("Attributes")
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("GeographicalAreas")
    public List<GeographicalArea> getGeographicalAreas() {
        return geographicalAreas;
    }

    @JsonProperty("GeographicalAreas")
    public void setGeographicalAreas(List<GeographicalArea> geographicalAreas) {
        this.geographicalAreas = geographicalAreas;
    }

    @JsonProperty("GeographicalPosition")
    public GeographicalPosition getGeographicalPosition() {
        return geographicalPosition;
    }

    @JsonProperty("GeographicalPosition")
    public void setGeographicalPosition(GeographicalPosition geographicalPosition) {
        this.geographicalPosition = geographicalPosition;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("RelatedServiceUnits")
    public List<Object> getRelatedServiceUnits() {
        return relatedServiceUnits;
    }

    @JsonProperty("RelatedServiceUnits")
    public void setRelatedServiceUnits(List<Object> relatedServiceUnits) {
        this.relatedServiceUnits = relatedServiceUnits;
    }

    @JsonProperty("ServiceUnitTypes")
    public List<ServiceUnitType> getServiceUnitTypes() {
        return serviceUnitTypes;
    }

    @JsonProperty("ServiceUnitTypes")
    public void setServiceUnitTypes(List<ServiceUnitType> serviceUnitTypes) {
        this.serviceUnitTypes = serviceUnitTypes;
    }

    @JsonProperty("TimeCreated")
    public String getTimeCreated() {
        return timeCreated;
    }

    @JsonProperty("TimeCreated")
    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    @JsonProperty("TimeUpdated")
    public String getTimeUpdated() {
        return timeUpdated;
    }

    @JsonProperty("TimeUpdated")
    public void setTimeUpdated(String timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
