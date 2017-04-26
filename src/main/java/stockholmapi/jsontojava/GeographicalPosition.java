
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
    "X",
    "Y"
})
public class GeographicalPosition {

    @JsonProperty("X")
    private Integer x;
    @JsonProperty("Y")
    private Integer y;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("X")
    public Integer getX() {
        return x;
    }

    @JsonProperty("X")
    public void setX(Integer x) {
        this.x = x;
    }

    @JsonProperty("Y")
    public Integer getY() {
        return y;
    }

    @JsonProperty("Y")
    public void setY(Integer y) {
        this.y = y;
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
