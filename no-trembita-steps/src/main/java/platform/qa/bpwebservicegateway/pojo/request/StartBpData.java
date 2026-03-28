package platform.qa.bpwebservicegateway.pojo.request;

import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartBpData {
    private String businessProcessDefinitionKey;
    private StartVariables startVariables;
}