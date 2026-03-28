package platform.qa.registry.management.dto.request.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFormRequest implements IEntity {
    private String name;
    private String title;
    private String display;
}