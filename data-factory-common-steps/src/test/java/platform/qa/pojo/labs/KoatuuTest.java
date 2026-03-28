package platform.qa.pojo.labs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KoatuuTest {

  @Test
  @DisplayName("Builder should correctly construct Koatuu")
  void testBuilder() {
    Koatuu obj =
        Koatuu.builder()
            .koatuuId("ID1")
            .level2("L2")
            .category("CAT")
            .code("12345")
            .level1("L1")
            .name("Kyiv")
            .type("city")
            .build();

    assertThat(obj.getKoatuuId()).isEqualTo("ID1");
    assertThat(obj.getLevel2()).isEqualTo("L2");
    assertThat(obj.getCategory()).isEqualTo("CAT");
    assertThat(obj.getCode()).isEqualTo("12345");
    assertThat(obj.getLevel1()).isEqualTo("L1");
    assertThat(obj.getName()).isEqualTo("Kyiv");
    assertThat(obj.getType()).isEqualTo("city");
  }

  @Test
  @DisplayName("toBuilder should create a modifiable copy")
  void testToBuilder() {
    Koatuu original =
        Koatuu.builder()
            .koatuuId("ID2")
            .level2("L2")
            .category("CAT")
            .code("987")
            .level1("L1")
            .name("Lviv")
            .type("region")
            .build();

    Koatuu modified = original.toBuilder().name("UpdatedName").build();

    assertThat(modified.getKoatuuId()).isEqualTo("ID2");
    assertThat(modified.getLevel2()).isEqualTo("L2");
    assertThat(modified.getCategory()).isEqualTo("CAT");
    assertThat(modified.getCode()).isEqualTo("987");
    assertThat(modified.getLevel1()).isEqualTo("L1");
    assertThat(modified.getName()).isEqualTo("UpdatedName");
    assertThat(modified.getType()).isEqualTo("region");
  }

  @Test
  @DisplayName("No-args constructor should create empty object")
  void testNoArgsConstructor() {
    Koatuu obj = new Koatuu();

    assertThat(obj.getKoatuuId()).isNull();
    assertThat(obj.getLevel2()).isNull();
    assertThat(obj.getCategory()).isNull();
    assertThat(obj.getCode()).isNull();
    assertThat(obj.getLevel1()).isNull();
    assertThat(obj.getName()).isNull();
    assertThat(obj.getType()).isNull();
  }

  @Test
  @DisplayName("All-args constructor should set all fields")
  void testAllArgsConstructor() {
    Koatuu obj = new Koatuu("ID3", "L2v", "CATv", "CDE", "L1v", "Odessa", "region");

    assertThat(obj.getKoatuuId()).isEqualTo("ID3");
    assertThat(obj.getLevel2()).isEqualTo("L2v");
    assertThat(obj.getCategory()).isEqualTo("CATv");
    assertThat(obj.getCode()).isEqualTo("CDE");
    assertThat(obj.getLevel1()).isEqualTo("L1v");
    assertThat(obj.getName()).isEqualTo("Odessa");
    assertThat(obj.getType()).isEqualTo("region");
  }

  @Test
  @DisplayName("getEmptyEntity should return empty Koatuu instance")
  void testGetEmptyEntity() {
    Koatuu empty = Koatuu.getEmptyEntity();

    assertThat(empty).isNotNull();
    assertThat(empty.getKoatuuId()).isNull();
    assertThat(empty.getLevel2()).isNull();
    assertThat(empty.getCategory()).isNull();
    assertThat(empty.getCode()).isNull();
    assertThat(empty.getLevel1()).isNull();
    assertThat(empty.getName()).isNull();
    assertThat(empty.getType()).isNull();
  }

  @Test
  @DisplayName("equals and hashCode should work correctly")
  void testEqualsAndHashCode() {
    Koatuu a = new Koatuu("X", "L2", "C", "777", "L1", "Dnipro", "city");
    Koatuu b = new Koatuu("X", "L2", "C", "777", "L1", "Dnipro", "city");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }
}
