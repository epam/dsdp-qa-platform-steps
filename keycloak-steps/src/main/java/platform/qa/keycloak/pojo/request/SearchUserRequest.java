package platform.qa.keycloak.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;
import platform.qa.entities.User;
import platform.qa.keycloak.enums.UserAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserRequest implements IEntity {
    private Map<String, String> attributes;

    public static SearchUserRequest prepareSearchRequest(UserAttributes attribute, String attributeValue) {
        return SearchUserRequest.builder()
                .attributes(new HashMap<>() {{
                    put(attribute.getAttributeName(), attributeValue);
                }}).build();
    }

    public static SearchUserRequest prepareSearchRequest(UserAttributes attribute, User searchUser) {
        return SearchUserRequest.builder()
                .attributes(new HashMap<>() {{
                    put(attribute.getAttributeName(),
                            searchUser.getAttributes().get(attribute.getAttributeName()).get(0));
                }}).build();
    }

    public static SearchUserRequest prepareSearchRequest(List<UserAttributes> attributes, User searchUser) {
        return SearchUserRequest.builder()
                .attributes(new HashMap<>() {{
                    for (UserAttributes attribute : attributes) {
                        put(attribute.getAttributeName(),
                                searchUser.getAttributes().get(attribute.getAttributeName()).get(0));
                    }
                }}).build();
    }
}
