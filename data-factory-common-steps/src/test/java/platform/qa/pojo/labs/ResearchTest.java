package platform.qa.pojo.labs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResearchTest {

  @Test
  @DisplayName("Builder should correctly construct Research")
  void testBuilder() {
    Research obj = Research.builder().researchId("R1").researchType("Medical").build();

    assertThat(obj.getResearchId()).isEqualTo("R1");
    assertThat(obj.getResearchType()).isEqualTo("Medical");
  }

  @Test
  @DisplayName("toBuilder should create a modifiable copy")
  void testToBuilder() {
    Research original = Research.builder().researchId("ID10").researchType("Chemical").build();

    Research modified = original.toBuilder().researchType("Biological").build();

    assertThat(modified.getResearchId()).isEqualTo("ID10");
    assertThat(modified.getResearchType()).isEqualTo("Biological");
  }

  @Test
  @DisplayName("No-args constructor should create empty object")
  void testNoArgsConstructor() {
    Research obj = new Research();

    assertThat(obj.getResearchId()).isNull();
    assertThat(obj.getResearchType()).isNull();
  }

  @Test
  @DisplayName("All-args constructor should set fields")
  void testAllArgsConstructor() {
    Research obj = new Research("R22", "Toxicology");

    assertThat(obj.getResearchId()).isEqualTo("R22");
    assertThat(obj.getResearchType()).isEqualTo("Toxicology");
  }

  @Test
  @DisplayName("getEmptyEntity() should return new empty Research instance")
  void testGetEmptyEntity() {
    Research empty = Research.getEmptyEntity();

    assertThat(empty).isNotNull();
    assertThat(empty.getResearchId()).isNull();
    assertThat(empty.getResearchType()).isNull();
  }

  @Test
  @DisplayName("equals and hashCode should work correctly")
  void testEqualsAndHashCode() {
    Research a = new Research("A1", "TypeA");
    Research b = new Research("A1", "TypeA");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }
}
