
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
    "FriendlyId",
    "Id",
    "IsCityArea",
    "Name"
})
public class GeographicalArea {

    @JsonProperty("FriendlyId")
    private String friendlyId;
    @JsonProperty("Id")
    private Integer id;
    @JsonProperty("IsCityArea")
    private Boolean isCityArea;
    @JsonProperty("Name")
    private String name;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("FriendlyId")
    public String getFriendlyId() {
        return friendlyId;
    }

    @JsonProperty("FriendlyId")
    public void setFriendlyId(String friendlyId) {
        this.friendlyId = friendlyId;
    }

    @JsonProperty("Id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("IsCityArea")
    public Boolean getIsCityArea() {
        return isCityArea;
    }

    @JsonProperty("IsCityArea")
    public void setIsCityArea(Boolean isCityArea) {
        this.isCityArea = isCityArea;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
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
