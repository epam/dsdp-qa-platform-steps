package platform.qa.pojo.map;

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
public class EntityGeoTypeManyDots implements IEntity {
    private String id;
    private String address;
    private String name;
    private EntityLocationDots entityLocation;
}