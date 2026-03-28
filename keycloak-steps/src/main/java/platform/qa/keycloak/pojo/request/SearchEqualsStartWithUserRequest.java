package platform.qa.keycloak.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchEqualsStartWithUserRequest implements IEntity {
    private SearchEqualsAttributes attributesEquals;
    private SearchStartWithAttributes attributesStartsWith;
}
