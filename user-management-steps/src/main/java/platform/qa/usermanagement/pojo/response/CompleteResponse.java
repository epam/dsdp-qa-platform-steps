package platform.qa.usermanagement.pojo.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class CompleteResponse {
    @JsonProperty
    private String id;
    @JsonProperty
    private String processInstanceId;
    @JsonProperty
    private String rootProcessInstanceId;
    @JsonProperty
    private boolean rootProcessInstanceEnded;
    @JsonProperty
    private Map variables;
}
