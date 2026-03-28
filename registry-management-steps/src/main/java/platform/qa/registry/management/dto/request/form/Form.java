package platform.qa.registry.management.dto.request.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Form implements IEntity {
    public String type;
    public List<Components> components;
    public String title;
    public String path;
    public String name;
    public String display;
    public List<Object> submissionAccess;
}
