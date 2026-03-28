package platform.qa.pojo.hierarchymanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class DocumentRegistration implements IEntity {

    private String id;
    private String name;
    private String hierarchyCode;
    private String unitId;
}
