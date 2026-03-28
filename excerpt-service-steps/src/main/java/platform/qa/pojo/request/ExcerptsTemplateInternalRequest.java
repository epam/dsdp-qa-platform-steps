package platform.qa.pojo.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExcerptsTemplateInternalRequest {
  private String templateName;
  private String template;
  private String createdAt;
  private String updatedAt;
  private String checksum;
  private String templateType;
}
