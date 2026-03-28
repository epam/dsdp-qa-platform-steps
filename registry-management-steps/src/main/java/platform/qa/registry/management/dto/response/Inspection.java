package platform.qa.registry.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.registry.management.enumeration.Result;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inspection {
    private String inspector;
    private String name;
    private String resultDetails;
    private Result result;
}
