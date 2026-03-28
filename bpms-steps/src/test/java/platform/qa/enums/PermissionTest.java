package platform.qa.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PermissionTest {

  @Test
  void values_shouldContainAllPermissions() {
    Permission[] values = Permission.values();

    assertThat(values)
        .isNotEmpty()
        .contains(
            Permission.NONE,
            Permission.ALL,
            Permission.READ,
            Permission.UPDATE,
            Permission.CREATE,
            Permission.DELETE,
            Permission.ACCESS,
            Permission.READ_TASK,
            Permission.UPDATE_TASK,
            Permission.TASK_WORK,
            Permission.TASK_ASSIGN,
            Permission.CREATE_INSTANCE,
            Permission.READ_INSTANCE,
            Permission.UPDATE_INSTANCE,
            Permission.MIGRATE_INSTANCE,
            Permission.DELETE_INSTANCE,
            Permission.READ_HISTORY,
            Permission.DELETE_HISTORY);
  }

  @Test
  void valueOf_shouldReturnCorrectEnum() {
    Permission permission = Permission.valueOf("READ");

    assertThat(permission).isEqualTo(Permission.READ);
  }

  @Test
  void getter_shouldReturnPermissionString() {
    Permission permission = Permission.UPDATE_TASK;

    assertThat(permission.getPermission()).isEqualTo("UPDATE_TASK");
  }

  @Test
  void allEnumValues_shouldHaveMatchingPermissionField() {
    for (Permission permission : Permission.values()) {
      assertThat(permission.getPermission())
          .as("Permission string should match enum name")
          .isEqualTo(permission.name());
    }
  }
}
