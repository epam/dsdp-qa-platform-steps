package platform.qa.registry.management.dto.request.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conditional {
    private String show;
    private String when;
    private String eq;
    private String json;
}
