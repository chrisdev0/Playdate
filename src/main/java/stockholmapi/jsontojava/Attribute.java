
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
    "Description",
    "Group",
    "GroupDescription",
    "Id",
    "Name",
    "Value",
    "Value2",
    "ServiceUnitTypeInfo",
    "Values"
})
public class Attribute {

    @JsonProperty("Description")
    private String description;
    @JsonProperty("Group")
    private String group;
    @JsonProperty("GroupDescription")
    private Object groupDescription;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Value")
    private String value;
    @JsonProperty("Value2")
    private Value2 value2;
    @JsonProperty("ServiceUnitTypeInfo")
    private ServiceUnitTypeInfo serviceUnitTypeInfo;
    @JsonProperty("Values")
    private List<String> values = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("Group")
    public String getGroup() {
        return group;
    }

    @JsonProperty("Group")
    public void setGroup(String group) {
        this.group = group;
    }

    @JsonProperty("GroupDescription")
    public Object getGroupDescription() {
        return groupDescription;
    }

    @JsonProperty("GroupDescription")
    public void setGroupDescription(Object groupDescription) {
        this.groupDescription = groupDescription;
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

    @JsonProperty("Value")
    public String getValue() {
        return value;
    }

    @JsonProperty("Value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("Value2")
    public Value2 getValue2() {
        return value2;
    }

    @JsonProperty("Value2")
    public void setValue2(Value2 value2) {
        this.value2 = value2;
    }

    @JsonProperty("ServiceUnitTypeInfo")
    public ServiceUnitTypeInfo getServiceUnitTypeInfo() {
        return serviceUnitTypeInfo;
    }

    @JsonProperty("ServiceUnitTypeInfo")
    public void setServiceUnitTypeInfo(ServiceUnitTypeInfo serviceUnitTypeInfo) {
        this.serviceUnitTypeInfo = serviceUnitTypeInfo;
    }

    @JsonProperty("Values")
    public List<String> getValues() {
        return values;
    }

    @JsonProperty("Values")
    public void setValues(List<String> values) {
        this.values = values;
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
