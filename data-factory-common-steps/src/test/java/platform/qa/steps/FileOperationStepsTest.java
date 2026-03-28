package platform.qa.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import platform.qa.ceph.CephClient;
import platform.qa.pojo.consent.files.FileData;
import platform.qa.utils.CephUtils;

class FileOperationStepsTest {

  private CephClient cephClient;
  private FileOperationSteps steps;

  @BeforeEach
  void setup() {
    cephClient = mock(CephClient.class);
    steps = new FileOperationSteps(cephClient);
  }

  // -------------------------------------------------------------------------------------
  // createFileInCephBucketWithMetadata
  // -------------------------------------------------------------------------------------
  @Test
  void testCreateFileInCephBucketWithMetadata_success() {
    FileData fd = mock(FileData.class);
    File file = mock(File.class);
    ObjectMetadata md = new ObjectMetadata();

    when(fd.getId()).thenReturn("F123");
    when(fd.getFile()).thenReturn(file);
    when(fd.getMetadata()).thenReturn(md);

    try (MockedConstruction<FileInputStream> ignored =
        Mockito.mockConstruction(FileInputStream.class)) {

      steps.createFileInCephBucketWithMetadata("bucket", fd);

      verify(cephClient)
          .saveFileInBucket(eq("bucket"), eq("F123"), any(FileInputStream.class), eq(md));
    }
  }

  // -------------------------------------------------------------------------------------
  // createFileInCephBucket
  // -------------------------------------------------------------------------------------
  @Test
  void testCreateFileInCephBucket_success() {
    FileData fd = mock(FileData.class);
    File file = mock(File.class);

    when(fd.getId()).thenReturn("F1");
    when(fd.getFile()).thenReturn(file);

    steps.createFileInCephBucket("bucket", fd);

    verify(cephClient).saveFileInBucket("bucket", "F1", file);
  }

  // -------------------------------------------------------------------------------------
  // createMultipleFilesInCephBucket
  // -------------------------------------------------------------------------------------
  @Test
  void testCreateMultipleFilesInCephBucket_success() {

    FileData fd = mock(FileData.class);
    File file = mock(File.class);

    when(fd.getRealFileId()).thenReturn("123");
    when(fd.getFile()).thenReturn(file);

    try (MockedStatic<CephUtils> mocked = Mockito.mockStatic(CephUtils.class)) {

      mocked
          .when(() -> CephUtils.convertMultipleFileIdToStructuredKey("123"))
          .thenReturn("process/XYZ/123");

      steps.createMultipleFilesInCephBucket("bucket", List.of(fd));

      verify(cephClient).saveFileInBucket("bucket", "process/XYZ/123", file);
    }
  }

  // -------------------------------------------------------------------------------------
  // getFileFromBucket
  // -------------------------------------------------------------------------------------
  @Test
  void testGetFileFromBucket_success() {
    File f = mock(File.class);

    when(cephClient.getFileFromBucket("bucket", "key")).thenReturn(f);

    File result = steps.getFileFromBucket("bucket", "key");

    assertThat(result).isEqualTo(f);
  }

  // -------------------------------------------------------------------------------------
  // getFileMetadata
  // -------------------------------------------------------------------------------------
  @Test
  void testGetFileMetadata_success() {
    ObjectMetadata md = new ObjectMetadata();

    when(cephClient.getObjectMetadata("bucket", "key")).thenReturn(md);

    ObjectMetadata result = steps.getFileMetadataFromBucket("bucket", "key");

    assertThat(result).isEqualTo(md);
  }

  // -------------------------------------------------------------------------------------
  // deleteFileFromBucket
  // -------------------------------------------------------------------------------------
  @Test
  void testDeleteFileFromBucket_success() {
    steps.deleteFileFromBucket("bucket", "key");

    verify(cephClient).deleteFileFromBucket("bucket", "key");
  }

  // -------------------------------------------------------------------------------------
  // deleteMultipleFilesInCephBucket
  // -------------------------------------------------------------------------------------
  @Test
  void testDeleteMultipleFilesInCephBucket_success() {
    FileData fd = mock(FileData.class);

    when(fd.getId()).thenReturn("DEL123");

    steps.deleteMultipleFilesInCephBucket("bucket", List.of(fd));

    verify(cephClient).deleteFileFromBucket("bucket", "DEL123");
  }
}
