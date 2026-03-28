package platform.qa.registry.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVersionCandidateRequest implements IEntity {
    private String name;
    private String description;
}
