package platform.qa.registry.management.dto.grouping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.registry.management.dto.request.bp.BusinessProcess;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpMetadata {
    private List<BusinessProcess> businessProcesses;
    private BpGrouping bpGroups;
}
