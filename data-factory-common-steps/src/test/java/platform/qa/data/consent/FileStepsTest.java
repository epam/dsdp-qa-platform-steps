package platform.qa.data.consent;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.IEntity;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.consent.files.FileData;
import platform.qa.pojo.consent.files.ScanCopy;
import platform.qa.utils.CephUtils;
import platform.qa.utils.FileHelper;

class FileStepsTest {

  private WireMockServer wireMock;
  private Service dfService;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("usr", "123");
    user.setToken("token");
    dfService = new Service("http://localhost:" + wireMock.port(), user);
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  @Test
  void testGetFileData() {
    FileData fd = FileHelper.getFileData("src/test/resources/testfile.txt");

    assertThat(fd).isNotNull();
    assertThat(fd.getFile()).exists();
  }

  @Test
  void testGetScanCopy() throws IOException {
    File tempFile = File.createTempFile("test", ".txt");

    FileData fd = new FileData();
    fd.setFile(tempFile);
    fd.setRealFileId("ABC123");

    ScanCopy sc =
        ScanCopy.builder()
            .checksum(CephUtils.getChecksumOfFile(fd.getFile()))
            .id(fd.getRealFileId())
            .build();

    assertThat(sc.getId()).isEqualTo("ABC123");
    assertThat(sc.getChecksum()).isNotNull();
  }

  @Test
  void testCreateFile_success() {
    stubFor(
        post(urlEqualTo("/upload"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"FILE123\"}")));

    FileSteps steps;

    IEntity payload = new TestEntity("test");

    try (MockedConstruction<SignatureSteps> mocked =
        Mockito.mockConstruction(
            SignatureSteps.class,
            (mock, ctx) -> Mockito.when(mock.signRequest(any())).thenReturn("SIGN-1"))) {

      steps = new FileSteps(dfService, dfService, List.of());

      String id = steps.createFile(payload, "/upload", "PROC55");

      assertThat(id).isEqualTo("FILE123");

      SignatureSteps created = mocked.constructed().get(0);
      Mockito.verify(created).signRequest(payload);
    }
  }

  record TestEntity(String value) implements IEntity {}
}
