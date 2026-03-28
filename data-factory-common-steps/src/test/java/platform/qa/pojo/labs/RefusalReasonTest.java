package platform.qa.pojo.labs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefusalReasonTest {

  @Test
  @DisplayName("Builder should correctly construct RefusalReason")
  void testBuilder() {
    RefusalReason obj =
        RefusalReason.builder()
            .refusalReasonId("R1")
            .documentType("DOC")
            .constantCode("C123")
            .name("Incorrect data")
            .build();

    assertThat(obj.getRefusalReasonId()).isEqualTo("R1");
    assertThat(obj.getDocumentType()).isEqualTo("DOC");
    assertThat(obj.getConstantCode()).isEqualTo("C123");
    assertThat(obj.getName()).isEqualTo("Incorrect data");
  }

  @Test
  @DisplayName("toBuilder should create a modifiable copy")
  void testToBuilder() {
    RefusalReason original =
        RefusalReason.builder()
            .refusalReasonId("X1")
            .documentType("FORM")
            .constantCode("CODE1")
            .name("Old Name")
            .build();

    RefusalReason modified = original.toBuilder().name("New Name").build();

    assertThat(modified.getRefusalReasonId()).isEqualTo("X1");
    assertThat(modified.getDocumentType()).isEqualTo("FORM");
    assertThat(modified.getConstantCode()).isEqualTo("CODE1");
    assertThat(modified.getName()).isEqualTo("New Name");
  }

  @Test
  @DisplayName("No-args constructor should create empty object")
  void testNoArgsConstructor() {
    RefusalReason obj = new RefusalReason();

    assertThat(obj.getRefusalReasonId()).isNull();
    assertThat(obj.getDocumentType()).isNull();
    assertThat(obj.getConstantCode()).isNull();
    assertThat(obj.getName()).isNull();
  }

  @Test
  @DisplayName("All-args constructor should set all fields")
  void testAllArgsConstructor() {
    RefusalReason obj = new RefusalReason("RR1", "PASSPORT", "C999", "Missing stamps");

    assertThat(obj.getRefusalReasonId()).isEqualTo("RR1");
    assertThat(obj.getDocumentType()).isEqualTo("PASSPORT");
    assertThat(obj.getConstantCode()).isEqualTo("C999");
    assertThat(obj.getName()).isEqualTo("Missing stamps");
  }

  @Test
  @DisplayName("getEmptyEntity() should return new empty object")
  void testGetEmptyEntity() {
    RefusalReason empty = RefusalReason.getEmptyEntity();

    assertThat(empty).isNotNull();
    assertThat(empty.getRefusalReasonId()).isNull();
    assertThat(empty.getDocumentType()).isNull();
    assertThat(empty.getConstantCode()).isNull();
    assertThat(empty.getName()).isNull();
  }

  @Test
  @DisplayName("equals() and hashCode() should work correctly")
  void testEqualsAndHashCode() {
    RefusalReason a = new RefusalReason("ID7", "FORM", "C1", "Reason A");
    RefusalReason b = new RefusalReason("ID7", "FORM", "C1", "Reason A");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }
}
