package platform.qa.registry.management.dto.response.unsupported;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnsupportedCreateCandidateRequest implements IEntity {
    private String description;
}
