package platform.qa.bpwebservicegateway.pojo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartBpParams {
    private StartVariables startVariables;
}
