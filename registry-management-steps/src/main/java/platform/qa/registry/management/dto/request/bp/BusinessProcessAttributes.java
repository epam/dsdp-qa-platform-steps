package platform.qa.registry.management.dto.request.bp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProcessAttributes {
    private String updated;
    private String created;
}
