package platform.qa.registry.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedVersionResponse {
    private String id;
    private String name;
    private String description;
    private String author;
    private String creationDate;
    private String latestUpdate;
    private boolean hasConflicts;
    private List<Inspection> inspections;
    private List<Validation> validations;
}
