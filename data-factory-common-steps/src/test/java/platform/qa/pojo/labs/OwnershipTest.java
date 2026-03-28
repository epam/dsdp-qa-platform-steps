package platform.qa.pojo.labs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OwnershipTest {

  @Test
  @DisplayName("Builder should correctly construct Ownership")
  void testBuilder() {
    Ownership obj = Ownership.builder().ownershipId("O1").code("C123").name("Private").build();

    assertThat(obj.getOwnershipId()).isEqualTo("O1");
    assertThat(obj.getCode()).isEqualTo("C123");
    assertThat(obj.getName()).isEqualTo("Private");
  }

  @Test
  @DisplayName("toBuilder should create a modifiable copy")
  void testToBuilder() {
    Ownership original = Ownership.builder().ownershipId("OX").code("C1").name("State").build();

    Ownership changed = original.toBuilder().name("Municipal").build();

    assertThat(changed.getOwnershipId()).isEqualTo("OX");
    assertThat(changed.getCode()).isEqualTo("C1");
    assertThat(changed.getName()).isEqualTo("Municipal");
  }

  @Test
  @DisplayName("No-args constructor should create empty object")
  void testNoArgsConstructor() {
    Ownership obj = new Ownership();

    assertThat(obj.getOwnershipId()).isNull();
    assertThat(obj.getCode()).isNull();
    assertThat(obj.getName()).isNull();
  }

  @Test
  @DisplayName("All-args constructor should set all fields")
  void testAllArgsConstructor() {
    Ownership obj = new Ownership("O2", "C777", "Communal");

    assertThat(obj.getOwnershipId()).isEqualTo("O2");
    assertThat(obj.getCode()).isEqualTo("C777");
    assertThat(obj.getName()).isEqualTo("Communal");
  }

  @Test
  @DisplayName("getEmptyEntity should return empty instance")
  void testGetEmptyEntity() {
    Ownership empty = Ownership.getEmptyEntity();

    assertThat(empty).isNotNull();
    assertThat(empty.getOwnershipId()).isNull();
    assertThat(empty.getCode()).isNull();
    assertThat(empty.getName()).isNull();
  }

  @Test
  @DisplayName("equals and hashCode work correctly")
  void testEqualsAndHashCode() {
    Ownership a = new Ownership("R", "111", "Test");
    Ownership b = new Ownership("R", "111", "Test");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }
}
