package platform.qa.registry.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Groups {
    private String name;
    private List<BusinessProcessInfo> processDefinitions;
}
