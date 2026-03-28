package platform.qa.registry.management.dto.request.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Overlay {
    private String style;
    private String left;
    private String top;
    private String width;
    private String height;
    private String page;
}
