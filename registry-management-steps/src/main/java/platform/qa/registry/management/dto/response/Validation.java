package platform.qa.registry.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.registry.management.enumeration.Result;
import platform.qa.registry.management.enumeration.Type;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Validation {
    private String name;
    private Result result;
    private String resultDetails;
    private Type type;
}
