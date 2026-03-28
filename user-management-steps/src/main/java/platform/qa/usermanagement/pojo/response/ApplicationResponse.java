package platform.qa.usermanagement.pojo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApplicationResponse {
  @JsonProperty("id")
  private String id;

  @JsonProperty("status")
  private String status;

  @JsonProperty("steps")
  private List<StepInfo> steps;

  @Data
  public static class StepInfo {
    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("status")
    private String status;
}
}

