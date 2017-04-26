
package stockholmapi.jsontojava;

import java.util.HashMap;
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
    "GeographicalPosition",
    "Id",
    "Name",
    "TimeCreated",
    "TimeUpdated"
})
public class ServiceUnitTypes {

    @JsonProperty("GeographicalPosition")
    private GeographicalPosition geographicalPosition;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("TimeCreated")
    private String timeCreated;
    @JsonProperty("TimeUpdated")
    private String timeUpdated;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
