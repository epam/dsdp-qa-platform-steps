package platform.qa.pojo.labs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FactorTest {

  @Test
  @DisplayName("Builder should correctly construct Factor")
  void testBuilder() {
    Factor factor =
        Factor.builder()
            .factorId("F1")
            .notes("Important notes")
            .code("CODE123")
            .name("Factor Name")
            .factorType("TYPE_A")
            .build();

    assertThat(factor.getFactorId()).isEqualTo("F1");
    assertThat(factor.getNotes()).isEqualTo("Important notes");
    assertThat(factor.getCode()).isEqualTo("CODE123");
    assertThat(factor.getName()).isEqualTo("Factor Name");
    assertThat(factor.getFactorType()).isEqualTo("TYPE_A");
  }

  @Test
  @DisplayName("toBuilder should create a copy that can be modified")
  void testToBuilder() {
    Factor original =
        Factor.builder()
            .factorId("ID")
            .notes("Original")
            .code("C1")
            .name("Name1")
            .factorType("T1")
            .build();

    Factor modified = original.toBuilder().name("UpdatedName").build();

    assertThat(modified.getFactorId()).isEqualTo("ID");
    assertThat(modified.getCode()).isEqualTo("C1");
    assertThat(modified.getFactorType()).isEqualTo("T1");
    assertThat(modified.getName()).isEqualTo("UpdatedName");
    assertThat(modified.getNotes()).isEqualTo("Original");
  }

  @Test
  @DisplayName("No-args constructor should create empty Factor")
  void testNoArgsConstructor() {
    Factor factor = new Factor();

    assertThat(factor.getFactorId()).isNull();
    assertThat(factor.getNotes()).isNull();
    assertThat(factor.getCode()).isNull();
    assertThat(factor.getName()).isNull();
    assertThat(factor.getFactorType()).isNull();
  }

  @Test
  @DisplayName("All-args constructor should set all fields")
  void testAllArgsConstructor() {
    Factor factor = new Factor("F2", "NTS", "C2", "Name2", "T2");

    assertThat(factor.getFactorId()).isEqualTo("F2");
    assertThat(factor.getNotes()).isEqualTo("NTS");
    assertThat(factor.getCode()).isEqualTo("C2");
    assertThat(factor.getName()).isEqualTo("Name2");
    assertThat(factor.getFactorType()).isEqualTo("T2");
  }

  @Test
  @DisplayName("getEmptyEntity() should return a new empty Factor")
  void testGetEmptyEntity() {
    Factor empty = Factor.getEmptyEntity();

    assertThat(empty).isNotNull();
    assertThat(empty.getFactorId()).isNull();
    assertThat(empty.getNotes()).isNull();
    assertThat(empty.getCode()).isNull();
    assertThat(empty.getName()).isNull();
    assertThat(empty.getFactorType()).isNull();
  }

  @Test
  @DisplayName("equals() and hashCode() should work correctly")
  void testEqualsAndHashCode() {
    Factor a = new Factor("1", "n1", "c1", "name", "type");
    Factor b = new Factor("1", "n1", "c1", "name", "type");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }
}
