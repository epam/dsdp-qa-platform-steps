package platform.qa.pojo.labs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApplicationTypeTest {

  @Test
  @DisplayName("Builder should correctly construct ApplicationType")
  void testBuilder() {
    ApplicationType type =
        ApplicationType.builder()
            .applicationTypeId("123")
            .name("Test Name")
            .constantCode("CODE_1")
            .build();

    assertThat(type.getApplicationTypeId()).isEqualTo("123");
    assertThat(type.getName()).isEqualTo("Test Name");
    assertThat(type.getConstantCode()).isEqualTo("CODE_1");
  }

  @Test
  @DisplayName("toBuilder should create a modifiable copy")
  void testToBuilder() {
    ApplicationType original =
        ApplicationType.builder()
            .applicationTypeId("id1")
            .name("Original")
            .constantCode("CONST")
            .build();

    ApplicationType modified = original.toBuilder().name("Updated").build();

    assertThat(modified.getName()).isEqualTo("Updated");
    assertThat(modified.getApplicationTypeId()).isEqualTo("id1");
    assertThat(modified.getConstantCode()).isEqualTo("CONST");
  }

  @Test
  @DisplayName("No-args constructor should create empty object")
  void testNoArgsConstructor() {
    ApplicationType type = new ApplicationType();

    assertThat(type.getApplicationTypeId()).isNull();
    assertThat(type.getName()).isNull();
    assertThat(type.getConstantCode()).isNull();
  }

  @Test
  @DisplayName("All-args constructor should set all fields")
  void testAllArgsConstructor() {
    ApplicationType type = new ApplicationType("1", "A", "C");

    assertThat(type.getApplicationTypeId()).isEqualTo("1");
    assertThat(type.getName()).isEqualTo("A");
    assertThat(type.getConstantCode()).isEqualTo("C");
  }

  @Test
  @DisplayName("getEmptyEntity() should return new empty object")
  void testGetEmptyEntity() {
    ApplicationType empty = ApplicationType.getEmptyEntity();

    assertThat(empty).isNotNull();
    assertThat(empty.getApplicationTypeId()).isNull();
    assertThat(empty.getName()).isNull();
    assertThat(empty.getConstantCode()).isNull();
  }

  @Test
  @DisplayName("equals() and hashCode() should work correctly")
  void testEqualsAndHashCode() {
    ApplicationType a = new ApplicationType("1", "Name", "C");
    ApplicationType b = new ApplicationType("1", "Name", "C");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }
}
