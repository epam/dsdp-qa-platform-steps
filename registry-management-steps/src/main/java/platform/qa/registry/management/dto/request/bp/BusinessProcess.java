package platform.qa.registry.management.dto.request.bp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProcess {
    private String title;
    private String name;
    private String created;
    private String updated;
}
