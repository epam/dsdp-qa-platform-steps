package platform.qa.utils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import platform.qa.pojo.consent.files.FileData;

class FileHelperTest {

  @Test
  void testUtilityConstructor_throwsException() throws Exception {
    Constructor<FileHelper> ctor = FileHelper.class.getDeclaredConstructor();
    ctor.setAccessible(true);

    assertThatThrownBy(ctor::newInstance)
        .hasCauseInstanceOf(IllegalStateException.class)
        .hasRootCauseMessage("This is utility class!");
  }

  @Test
  void testGetFileData_success() {
    String fixedUUID = "11111111-2222-3333-4444-555555555555";

    UUID uuidObj = UUID.fromString(fixedUUID);

    String path = "/tmp/test-file.txt";

    try (MockedStatic<UUID> mocked = mockStatic(UUID.class)) {

      mocked.when(UUID::randomUUID).thenReturn(uuidObj);

      FileData fd = FileHelper.getFileData(path);

      assertThat(fd).isNotNull();
      assertThat(fd.getRealFileId()).isEqualTo(fixedUUID);

      String expectedKey = CephUtils.convertFileIdToStructuredKey(fixedUUID);
      assertThat(fd.getId()).isEqualTo(expectedKey);

      assertThat(fd.getMetadata()).isNotNull();
      assertThat(fd.getFile().getPath()).isEqualTo(path);
    }
  }
}
