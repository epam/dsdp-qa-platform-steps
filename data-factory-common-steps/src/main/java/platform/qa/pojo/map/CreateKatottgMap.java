package platform.qa.pojo.map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateKatottgMap implements IEntity {
    private String katottg;
    private String katottgMapId;
    private EntityLocationDots entityLocation;
}
