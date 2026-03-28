package platform.qa.utils;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class CephUtilsTest {

  // ------------------------------
  // UTILITY CLASS CONSTRUCTOR CHECK
  // ------------------------------
  @Test
  void testPrivateConstructor_throwsException() {
    assertThatThrownBy(
            () -> {
              var ctor = CephUtils.class.getDeclaredConstructor();
              ctor.setAccessible(true);
              ctor.newInstance();
            })
        .hasCauseInstanceOf(IllegalStateException.class)
        .hasRootCauseMessage("This is utility class!");
  }

  // ------------------------------
  // getChecksumOfFile()
  // ------------------------------
  @Test
  void testGetChecksumOfFile_success() throws Exception {
    File temp = File.createTempFile("chk", ".txt");
    temp.deleteOnExit();

    try (FileWriter fw = new FileWriter(temp, StandardCharsets.UTF_8)) {
      fw.write("hello world");
    }

    // sha256("hello world") = b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9
    String expected = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9";

    String checksum = CephUtils.getChecksumOfFile(temp);

    assertThat(checksum).isEqualTo(expected);
  }

  // ------------------------------
  // convertFileIdToStructuredKey(id)
  // ------------------------------
  @Test
  void testConvertFileIdToStructuredKey_simple() {
    String key = CephUtils.convertFileIdToStructuredKey("123");
    assertThat(key).isEqualTo("process/123/123");
  }

  // ------------------------------
  // convertMultipleFileIdToStructuredKey(id)
  // ------------------------------
  @Test
  void testConvertMultipleFileIdToStructuredKey_success() {
    String key = CephUtils.convertMultipleFileIdToStructuredKey("999");

    assertThat(key).isEqualTo("process/" + CephUtils.PROCESS_ID_FOR_MULTIPLE_FILES + "/999");
  }

  // ------------------------------
  // convertFileIdToStructuredKey(id, processId)
  // ------------------------------
  @Test
  void testConvertFileIdToStructuredKey_withProcessId() {
    String key = CephUtils.convertFileIdToStructuredKey("ABC", "PROC123");

    assertThat(key).isEqualTo("process/PROC123/ABC");
  }
}
