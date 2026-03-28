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
public class Type implements IEntity {

    private String typeId;
    private String name;
    private String code;

}
