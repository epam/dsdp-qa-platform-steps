package platform.qa.pojo.weapon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Model implements IEntity {

    private String modelId;
    private String name;
    private String code;

}
