package platform.qa.usermanagement.pojo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

@Data
public class StepResponse {
  @JsonProperty("id")
  private String id;

  @JsonProperty("type")
  private String type;

  @JsonProperty("status")
  private String status;

  @JsonProperty("details")
  private StepDetails details;

  @JsonProperty("form")
  private FormData form;

  @Data
  public static class StepDetails {
    @JsonProperty("verification_code")
    private String verificationCode;
}

  @Data
  public static class FormData {
    @JsonProperty("key")
    private String key;

    @JsonProperty("evalContext")
    private Map<String, Object> evalContext;

    @JsonProperty("data")
    private Map<String, Object> data;
}
}

