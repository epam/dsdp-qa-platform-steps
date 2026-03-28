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
public class Brand implements IEntity {

    private String brandId;
    private String name;
    private String code;

}
