package platform.qa.registry.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSettings {
    private String titleFull;
    private String title;
    private String theme;
    private String supportEmail;
    private String supportChannelUrl;
}
