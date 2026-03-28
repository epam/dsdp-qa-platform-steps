package platform.qa.registry.management.dto.response.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Table {
    private String name;
    private String description;
    private boolean objectReference;
}
