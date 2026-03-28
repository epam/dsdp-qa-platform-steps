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
public class MasterVersionInfoResponse {
    private String author;
    private String description;
    private String id;
    private String inspector;
    private String latestUpdate;
    private String name;
    private boolean published;
    private List<Validation> validations;
}
