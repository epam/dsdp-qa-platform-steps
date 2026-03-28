package platform.qa.usermanagement.pojo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateApplicationResponse {
  @JsonProperty("application")
  private ApplicationResponse application;
}

