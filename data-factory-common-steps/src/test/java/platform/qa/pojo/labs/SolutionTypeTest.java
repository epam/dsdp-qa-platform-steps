package platform.qa.pojo.labs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SolutionTypeTest {

  @Test
  @DisplayName("Builder should correctly construct SolutionType")
  void testBuilder() {
    SolutionType obj =
        SolutionType.builder().solutionTypeId("S1").constantCode("CONST").name("TestName").build();

    assertThat(obj.getSolutionTypeId()).isEqualTo("S1");
    assertThat(obj.getConstantCode()).isEqualTo("CONST");
    assertThat(obj.getName()).isEqualTo("TestName");
  }

  @Test
  @DisplayName("toBuilder should create a modifiable copy")
  void testToBuilder() {
    SolutionType original =
        SolutionType.builder().solutionTypeId("X1").constantCode("C1").name("Original").build();

    SolutionType modified = original.toBuilder().name("Updated").build();

    assertThat(modified.getSolutionTypeId()).isEqualTo("X1");
    assertThat(modified.getConstantCode()).isEqualTo("C1");
    assertThat(modified.getName()).isEqualTo("Updated");
  }

  @Test
  @DisplayName("No-args constructor should create empty object")
  void testNoArgsConstructor() {
    SolutionType obj = new SolutionType();

    assertThat(obj.getSolutionTypeId()).isNull();
    assertThat(obj.getConstantCode()).isNull();
    assertThat(obj.getName()).isNull();
  }

  @Test
  @DisplayName("All-args constructor should set all fields")
  void testAllArgsConstructor() {
    SolutionType obj = new SolutionType("ID7", "CC7", "SomeName");

    assertThat(obj.getSolutionTypeId()).isEqualTo("ID7");
    assertThat(obj.getConstantCode()).isEqualTo("CC7");
    assertThat(obj.getName()).isEqualTo("SomeName");
  }

  @Test
  @DisplayName("getEmptyEntity() should return new empty instance")
  void testGetEmptyEntity() {
    SolutionType empty = SolutionType.getEmptyEntity();

    assertThat(empty).isNotNull();
    assertThat(empty.getSolutionTypeId()).isNull();
    assertThat(empty.getConstantCode()).isNull();
    assertThat(empty.getName()).isNull();
  }

  @Test
  @DisplayName("equals() and hashCode() should work correctly")
  void testEqualsAndHashCode() {
    SolutionType a = new SolutionType("X", "C", "Name");
    SolutionType b = new SolutionType("X", "C", "Name");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }
}
