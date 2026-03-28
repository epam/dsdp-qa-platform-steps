package platform.qa.registry.management.dto.response.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableData {
    private String name;
    private String description;
    private boolean objectReference;
    private Map<String, Map<String, Object>> columns;
    private Map<String, Map<String, Object>> foreignKeys;
    private Map<String, Object> primaryKey;
    private Map<String, Map<String, Object>> uniqueConstraints;
    private Map<String, Map<String, Object>> indices;
}
