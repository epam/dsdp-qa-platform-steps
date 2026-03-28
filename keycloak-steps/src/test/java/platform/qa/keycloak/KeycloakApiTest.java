package platform.qa.keycloak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.keycloak.api.KeycloakApi;
import platform.qa.keycloak.pojo.request.SearchEqualsStartWithUserRequest;
import platform.qa.keycloak.pojo.request.SearchUserRequest;
import platform.qa.keycloak.pojo.response.KeycloakError;
import platform.qa.keycloak.pojo.response.KeycloakUser;
import platform.qa.keycloak.pojo.response.KeycloakUserResponse;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KeycloakApiTest {

    private KeycloakApi keycloakApi;
    private Service mockService;
    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockService = mock(Service.class);
        mockUser = mock(User.class);

        when(mockService.getUrl()).thenReturn("http://localhost:8080");
        when(mockUser.getToken()).thenReturn("mock-token");

        keycloakApi = mock(KeycloakApi.class);
    }

    @Test
    public void searchUsersByAttributesTest() {
        // Given
        SearchUserRequest request = SearchUserRequest.builder()
                .attributes(Map.of("drfo", "1234567890"))
                .build();

        List<KeycloakUser> expectedUsers = List.of(
                KeycloakUser.builder()
                        .username("testuser1")
                        .attributes(Map.of("drfo", List.of("1234567890")))
                        .build(),
                KeycloakUser.builder()
                        .username("testuser2")
                        .attributes(Map.of("drfo", List.of("1234567890")))
                        .build()
        );

        when(keycloakApi.searchUsersByAttributes(any(SearchUserRequest.class), anyString()))
                .thenReturn(expectedUsers);

        // When
        List<KeycloakUser> actualUsers = keycloakApi.searchUsersByAttributes(request, "test-realm");

        // Then
        assertThat(actualUsers).hasSize(2);
        assertThat(actualUsers).isEqualTo(expectedUsers);
        assertThat(actualUsers.get(0).getUsername()).isEqualTo("testuser1");
        assertThat(actualUsers.get(1).getUsername()).isEqualTo("testuser2");
    }

    @Test
    public void searchUserByInvalidAttributesTest() {
        // Given
        SearchUserRequest invalidRequest = SearchUserRequest.builder()
                .attributes(Map.of("invalidAttribute", "invalidValue"))
                .build();

        KeycloakError expectedError = KeycloakError.builder()
                .error("invalid_attribute")
                .build();

        when(keycloakApi.searchUserByInvalidAttributes(any(SearchUserRequest.class), anyString()))
                .thenReturn(expectedError);

        // When
        KeycloakError actualError = keycloakApi.searchUserByInvalidAttributes(invalidRequest, "test-realm");

        // Then
        assertThat(actualError).isNotNull();
        assertThat(actualError.getError()).isEqualTo("invalid_attribute");
    }

    @Test
    public void searchEqualsStartWithUserRequestTest() {
        // Given
        SearchEqualsStartWithUserRequest request = SearchEqualsStartWithUserRequest.builder()
                .build();

        KeycloakUserResponse expectedResponse = KeycloakUserResponse.builder()
                .users(List.of(
                        KeycloakUser.builder()
                                .username("user1")
                                .attributes(Map.of("fullName", List.of("John Doe")))
                                .build()
                ))
                .build();

        when(keycloakApi.searchEqualsStartWithUserRequest(any(SearchEqualsStartWithUserRequest.class), anyString()))
                .thenReturn(expectedResponse);

        // When
        KeycloakUserResponse actualResponse = keycloakApi.searchEqualsStartWithUserRequest(request, "test-realm");

        // Then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getUsers()).hasSize(1);
        assertThat(actualResponse.getUsers().get(0).getUsername()).isEqualTo("user1");
    }
}
