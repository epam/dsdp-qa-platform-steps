package platform.qa.notifications.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DeactivationTest {

  @Test
  @DisplayName("Enum value should exist and match expected name")
  void testEnumValueExists() {
    Deactivation value = Deactivation.USER_DEACTIVATED;

    assertThat(value).isNotNull();
    assertThat(value.name()).isEqualTo("USER_DEACTIVATED");
  }

  @Test
  @DisplayName("getReason() should return correct reason string")
  void testGetReason() {
    Deactivation value = Deactivation.USER_DEACTIVATED;

    assertThat(value.getReason()).isEqualTo("USER_DEACTIVATED");
  }
}
