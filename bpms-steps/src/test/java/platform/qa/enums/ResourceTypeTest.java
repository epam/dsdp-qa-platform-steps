package platform.qa.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResourceTypeTest {

  @Test
  void values_shouldContainAllResourceTypes() {
    assertThat(ResourceType.values())
        .isNotEmpty()
        .contains(
            ResourceType.APPLICATION,
            ResourceType.AUTHORIZATION,
            ResourceType.BATCH,
            ResourceType.DECISION_DEFINITION,
            ResourceType.DECISION_REQUIREMENTS_DEFINITION,
            ResourceType.DEPLOYMENT,
            ResourceType.FILTER,
            ResourceType.GROUP,
            ResourceType.GROUP_MEMBERSHIP,
            ResourceType.PROCESS_DEFINITION,
            ResourceType.PROCESS_INSTANCE,
            ResourceType.TASK,
            ResourceType.TENANT,
            ResourceType.TENANT_MEMBERSHIP,
            ResourceType.USER);
  }

  @Test
  void valueOf_shouldReturnCorrectEnum() {
    ResourceType type = ResourceType.valueOf("TASK");

    assertThat(type).isEqualTo(ResourceType.TASK);
  }

  @Test
  void getter_shouldReturnCorrectTypeValue() {
    assertThat(ResourceType.APPLICATION.getType()).isZero();
    assertThat(ResourceType.USER.getType()).isEqualTo(1);
    assertThat(ResourceType.GROUP.getType()).isEqualTo(2);
    assertThat(ResourceType.GROUP_MEMBERSHIP.getType()).isEqualTo(3);
    assertThat(ResourceType.AUTHORIZATION.getType()).isEqualTo(4);
    assertThat(ResourceType.FILTER.getType()).isEqualTo(5);
    assertThat(ResourceType.PROCESS_DEFINITION.getType()).isEqualTo(6);
    assertThat(ResourceType.TASK.getType()).isEqualTo(7);
    assertThat(ResourceType.PROCESS_INSTANCE.getType()).isEqualTo(8);
    assertThat(ResourceType.DEPLOYMENT.getType()).isEqualTo(9);
    assertThat(ResourceType.DECISION_DEFINITION.getType()).isEqualTo(10);
    assertThat(ResourceType.TENANT.getType()).isEqualTo(11);
    assertThat(ResourceType.TENANT_MEMBERSHIP.getType()).isEqualTo(12);
    assertThat(ResourceType.BATCH.getType()).isEqualTo(13);
    assertThat(ResourceType.DECISION_REQUIREMENTS_DEFINITION.getType()).isEqualTo(14);
  }

  @Test
  void allResourceTypes_shouldHaveUniqueTypeCodes() {
    assertThat(
            java.util.Arrays.stream(ResourceType.values())
                .map(ResourceType::getType)
                .distinct()
                .count())
        .isEqualTo(ResourceType.values().length);
  }
}
