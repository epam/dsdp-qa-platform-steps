package platform.qa.usermanagement.pojo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SubmitStepResponse {
  @JsonProperty("application")
  private ApplicationResponse application;
}

