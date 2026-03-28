package platform.qa.registry.management.dto.request.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Validate {
    private Boolean required;
    private String customMessage;
    private String custom;
    private Boolean customPrivate;
    private String json;
    private String minLength;
    private String maxLength;
    private Boolean strictDateValidation;
    private Boolean multiple;
    private Boolean unique;
}