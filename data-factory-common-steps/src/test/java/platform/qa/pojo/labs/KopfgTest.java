package platform.qa.pojo.labs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KopfgTest {

  @Test
  @DisplayName("Builder should correctly construct Kopfg")
  void testBuilder() {
    Kopfg obj = Kopfg.builder().kopfgId("ID1").code("C123").name("SomeName").build();

    assertThat(obj.getKopfgId()).isEqualTo("ID1");
    assertThat(obj.getCode()).isEqualTo("C123");
    assertThat(obj.getName()).isEqualTo("SomeName");
  }

  @Test
  @DisplayName("toBuilder should create a modifiable copy")
  void testToBuilder() {
    Kopfg original = Kopfg.builder().kopfgId("X1").code("CODE1").name("Original").build();

    Kopfg modified = original.toBuilder().name("Updated").build();

    assertThat(modified.getKopfgId()).isEqualTo("X1");
    assertThat(modified.getCode()).isEqualTo("CODE1");
    assertThat(modified.getName()).isEqualTo("Updated");
  }

  @Test
  @DisplayName("No-args constructor should create empty object")
  void testNoArgsConstructor() {
    Kopfg obj = new Kopfg();

    assertThat(obj.getKopfgId()).isNull();
    assertThat(obj.getCode()).isNull();
    assertThat(obj.getName()).isNull();
  }

  @Test
  @DisplayName("All-args constructor should set fields")
  void testAllArgsConstructor() {
    Kopfg obj = new Kopfg("A1", "CODEX", "Kyiv");

    assertThat(obj.getKopfgId()).isEqualTo("A1");
    assertThat(obj.getCode()).isEqualTo("CODEX");
    assertThat(obj.getName()).isEqualTo("Kyiv");
  }

  @Test
  @DisplayName("getEmptyEntity should return new empty object")
  void testGetEmptyEntity() {
    Kopfg empty = Kopfg.getEmptyEntity();

    assertThat(empty).isNotNull();
    assertThat(empty.getKopfgId()).isNull();
    assertThat(empty.getCode()).isNull();
    assertThat(empty.getName()).isNull();
  }

  @Test
  @DisplayName("equals and hashCode should work correctly")
  void testEqualsAndHashCode() {
    Kopfg a = new Kopfg("ZZ", "123", "Test");
    Kopfg b = new Kopfg("ZZ", "123", "Test");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }
}
