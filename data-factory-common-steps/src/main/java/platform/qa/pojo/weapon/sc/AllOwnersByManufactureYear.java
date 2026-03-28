package platform.qa.pojo.weapon.sc;

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
public class AllOwnersByManufactureYear implements IEntity {

    private String contacts;
    private String type;
    private String brand;
    private String model;
    private String name;
    private int manufactureYear;
}
