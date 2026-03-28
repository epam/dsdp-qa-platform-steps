package platform.qa.registry.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Change {
    private String name;
    private String title;
    private String status;
    private boolean conflicted;
}