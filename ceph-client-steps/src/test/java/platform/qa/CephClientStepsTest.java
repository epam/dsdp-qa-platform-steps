package platform.qa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import platform.qa.ceph.CephClient;
import platform.qa.entities.Ceph;

class CephClientStepsTest {

  private CephClientSteps createSteps(Ceph ceph) {
    return new CephClientSteps(ceph);
  }

  private Bucket mockBucket(String name) {
    Bucket bucket = mock(Bucket.class);
    when(bucket.getName()).thenReturn(name);
    return bucket;
  }

  // ---------------------------------------------------------
  // deleteAllFilesFromCeph()
  // ---------------------------------------------------------
  @Test
  void testDeleteAllFilesFromCeph() {
    Ceph ceph = mock(Ceph.class);
    Bucket bucket = mockBucket("bkt");

    try (MockedConstruction<CephClient> constr =
        Mockito.mockConstruction(
            CephClient.class,
            (mock, ctx) -> {
              when(mock.getBuckets()).thenReturn(List.of(bucket));
              when(mock.getListOfFilesFromBucket("bkt")).thenReturn(List.of("f1", "f2"));
            })) {

      CephClientSteps steps = createSteps(ceph);
      CephClient client = constr.constructed().get(0);

      steps.deleteAllFilesFromCeph();

      verify(client).deleteFileFromBucket("bkt", "f1");
      verify(client).deleteFileFromBucket("bkt", "f2");
    }
  }

  // ---------------------------------------------------------
  // checkFilesExistInCeph()
  // ---------------------------------------------------------
  @Test
  void testCheckFilesExistInCeph() {
    Ceph ceph = mock(Ceph.class);
    Bucket bucket = mockBucket("bkt");

    ObjectMetadata meta = new ObjectMetadata();
    meta.addUserMetadata("filename", "file1.txt");

    try (MockedConstruction<CephClient> constr =
        Mockito.mockConstruction(
            CephClient.class,
            (mock, ctx) -> {
              when(mock.getBuckets()).thenReturn(List.of(bucket));
              when(mock.getListOfFilesFromBucket("bkt"))
                  .thenReturn(List.of("proc123_abc", "ignore"));
              when(mock.getObjectMetadata("bkt", "proc123_abc")).thenReturn(meta);
            })) {

      CephClientSteps steps = createSteps(ceph);

      List<String> ids = steps.checkFilesExistInCeph(List.of("file1.txt"), "proc123");

      assertThat(ids).containsExactly("proc123_abc");
    }
  }

  // ---------------------------------------------------------
  // checkFilesDoNotExistInCeph()
  // ---------------------------------------------------------
  @Test
  void testCheckFilesDoNotExistInCeph() {
    Ceph ceph = mock(Ceph.class);
    Bucket bucket = mockBucket("bkt");

    try (MockedConstruction<CephClient> constr =
        Mockito.mockConstruction(
            CephClient.class,
            (mock, ctx) -> {
              when(mock.getBuckets()).thenReturn(List.of(bucket));
              when(mock.getListOfFilesFromBucket("bkt")).thenReturn(List.of("x1", "x2"));
            })) {

      CephClientSteps steps = createSteps(ceph);

      steps.checkFilesDoNotExistInCeph(List.of("gone1", "gone2"));
    }
  }

  // ---------------------------------------------------------
  // uploadFileToCeph()
  // ---------------------------------------------------------
  @Test
  void testUploadFileToCeph() throws Exception {
    Ceph ceph = mock(Ceph.class);
    Bucket bucket = mockBucket("bkt");

    File tmp = File.createTempFile("test", ".txt");
    IOUtils.write("hi", new FileOutputStream(tmp), "UTF-8");

    UUID fixed = UUID.fromString("11111111-1111-1111-1111-111111111111");

    try (MockedConstruction<CephClient> constr =
            Mockito.mockConstruction(
                CephClient.class,
                (mock, ctx) -> when(mock.getBuckets()).thenReturn(List.of(bucket)));
        MockedStatic<UUID> uuidMock = Mockito.mockStatic(UUID.class)) {

      uuidMock.when(UUID::randomUUID).thenReturn(fixed);

      CephClientSteps steps = createSteps(ceph);
      CephClient client = constr.constructed().get(0);

      Map result = steps.uploadFileToCeph(tmp, "proc1");

      assertThat(result.get("cephkey")).isEqualTo("process/proc1/" + fixed);
      assertThat(result.get("filename")).isEqualTo(tmp.getName());
      assertThat(result.get("checksum")).isNotNull();

      verify(client)
          .saveFileInBucket(
              eq("bkt"),
              eq("process/proc1/" + fixed),
              any(ByteArrayInputStream.class),
              any(ObjectMetadata.class));
    }
  }
}
