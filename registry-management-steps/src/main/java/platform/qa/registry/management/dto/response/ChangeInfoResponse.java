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
public class ChangeInfoResponse {
    private List<Change> changedForms;
    private List<Change> changedBusinessProcesses;
    private List<Change> changedDataModelFiles;
    private List<Change> changedGroups;
    private List<Change> changedI18nFiles;
}