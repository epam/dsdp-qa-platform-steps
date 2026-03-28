package platform.qa.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AuthorizationTypeTest {

  @Test
  void values_shouldContainAllAuthorizationTypes() {
    assertThat(AuthorizationType.values())
        .containsExactly(
            AuthorizationType.AUTH_TYPE_GLOBAL,
            AuthorizationType.AUTH_TYPE_GRANT,
            AuthorizationType.AUTH_TYPE_REVOKE);
  }

  @Test
  void valueOf_shouldReturnCorrectEnum() {
    AuthorizationType type = AuthorizationType.valueOf("AUTH_TYPE_GRANT");

    assertThat(type).isEqualTo(AuthorizationType.AUTH_TYPE_GRANT);
  }

  @Test
  void getter_shouldReturnCorrectTypeCode() {
    assertThat(AuthorizationType.AUTH_TYPE_GLOBAL.getType()).isZero();
    assertThat(AuthorizationType.AUTH_TYPE_GRANT.getType()).isEqualTo(1);
    assertThat(AuthorizationType.AUTH_TYPE_REVOKE.getType()).isEqualTo(2);
  }

  @Test
  void allAuthorizationTypes_shouldHaveUniqueTypeCodes() {
    assertThat(
            java.util.Arrays.stream(AuthorizationType.values())
                .map(AuthorizationType::getType)
                .distinct()
                .count())
        .isEqualTo(AuthorizationType.values().length);
  }
}
