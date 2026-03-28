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
public class VersionCandidateInfoResponse {
    private String author;
    private String creationDate;
    private String description;
    private boolean hasConflicts;
    private String id;
    private List<Inspection> inspections;
    private String latestUpdate;
    private String name;
    private boolean published;
    private List<Validation> validations;
}