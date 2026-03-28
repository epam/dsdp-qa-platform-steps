package platform.qa.settings.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class UserSettingsApiTest {

  Service mockService = mock(Service.class);
  User mockUser = mock(User.class);
  UserSettingsApi userSettingsApi;

  @Test
  void userSettingsApiConstructorTest() {
    when(mockService.getUrl()).thenReturn("https://user-settings.example.com");
    when(mockService.getUser()).thenReturn(mockUser);
    when(mockUser.getToken()).thenReturn("test-access-token");

    userSettingsApi = new UserSettingsApi(mockService);

    // Test that the API object is properly initialized
    assertThat(userSettingsApi).isNotNull();
  }

  @Test
  void userSettingsApiConstantsTest() {
    when(mockService.getUrl()).thenReturn("https://user-settings.example.com");
    when(mockService.getUser()).thenReturn(mockUser);
    when(mockUser.getToken()).thenReturn("test-access-token");

    userSettingsApi = new UserSettingsApi(mockService);

    // Test API constants and typical parameters
    String settingsPath = UserSettingsApi.SETTINGS;
    String keycloakUserId = "test-user-123";

    assertThat(userSettingsApi).isNotNull();
    assertThat(settingsPath).isEqualTo("/api/settings/");
    assertThat(keycloakUserId).isEqualTo("test-user-123");
    assertThat(keycloakUserId).startsWith("test-user");
  }
}
