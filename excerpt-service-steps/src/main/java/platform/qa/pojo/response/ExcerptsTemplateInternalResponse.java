package platform.qa.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcerptsTemplateInternalResponse {
  private String id;
  private String templateName;
  private String template;
  private String createdAt;
  private String updatedAt;
  private String checksum;
  private String templateType;
}
