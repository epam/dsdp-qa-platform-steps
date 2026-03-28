package platform.qa.pojo.petstore.sc;

import lombok.*;
import platform.qa.entities.IEntity;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PetStoreByName implements IEntity {
    private String name;
    private String owner;
    private String serialNumber;
}
