
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
    "DefinitiveName",
    "Id",
    "PluralName",
    "ServiceUnitTypeGroupInfo",
    "SingularName",
    "TimeCreated",
    "TimeUpdated"
})
public class ServiceUnitType {

    @JsonProperty("DefinitiveName")
    private String definitiveName;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("PluralName")
    private String pluralName;
    @JsonProperty("ServiceUnitTypeGroupInfo")
    private ServiceUnitTypeGroupInfo serviceUnitTypeGroupInfo;
    @JsonProperty("SingularName")
    private String singularName;
    @JsonProperty("TimeCreated")
    private String timeCreated;
    @JsonProperty("TimeUpdated")
    private String timeUpdated;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("DefinitiveName")
    public String getDefinitiveName() {
        return definitiveName;
    }

    @JsonProperty("DefinitiveName")
    public void setDefinitiveName(String definitiveName) {
        this.definitiveName = definitiveName;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("PluralName")
    public String getPluralName() {
        return pluralName;
    }

    @JsonProperty("PluralName")
    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }

    @JsonProperty("ServiceUnitTypeGroupInfo")
    public ServiceUnitTypeGroupInfo getServiceUnitTypeGroupInfo() {
        return serviceUnitTypeGroupInfo;
    }

    @JsonProperty("ServiceUnitTypeGroupInfo")
    public void setServiceUnitTypeGroupInfo(ServiceUnitTypeGroupInfo serviceUnitTypeGroupInfo) {
        this.serviceUnitTypeGroupInfo = serviceUnitTypeGroupInfo;
    }

    @JsonProperty("SingularName")
    public String getSingularName() {
        return singularName;
    }

    @JsonProperty("SingularName")
    public void setSingularName(String singularName) {
        this.singularName = singularName;
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
