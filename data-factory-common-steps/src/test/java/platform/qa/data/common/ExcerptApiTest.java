package platform.qa.data.common;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import platform.qa.data.entities.ExcerptFormats;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.common.ExportRequest;
import platform.qa.redis.JedisClient;

class ExcerptApiTest {

  MockedConstruction<JedisClient> mockedJedis;
  private WireMockServer wireMock;
  private ExcerptApi api;
  private Service service;
  private SignatureSteps signatureMock;

  @BeforeEach
  void setup() throws NoSuchFieldException, IllegalAccessException {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("usr", "pwd");
    user.setToken("token");

    service = new Service("http://localhost:" + wireMock.port(), user);

    mockedJedis =
        Mockito.mockConstruction(
            JedisClient.class,
            (mock, ctx) -> {
              when(mock.hset(anyString(), anyString(), anyString())).thenReturn(1L);
              doNothing().when(mock).close();
            });

    signatureMock = Mockito.mock(SignatureSteps.class);
    when(signatureMock.signRequest(any(Object.class))).thenReturn("SIGN123");

    when(signatureMock.signDeleteRequest(any(String.class))).thenReturn("DEL123");

    api = new ExcerptApi(service, service, List.of(new Redis("localhost", "pwd")));

    Field f = ExcerptApi.class.getDeclaredField("signatureSteps");
    f.setAccessible(true);
    f.set(api, signatureMock);
  }

  @AfterEach
  void teardown() {
    wireMock.stop();
    mockedJedis.close();
  }

  // -------------------------------
  // createExport
  // -------------------------------

  @Test
  void testCreateExport_success() {
    stubFor(post(urlEqualTo("/excerpts")).willReturn(okJson("{\"excerptIdentifier\": \"EX123\"}")));

    ExportRequest req = new ExportRequest();
    String id = api.createExport(req);

    assertThat(id).isEqualTo("EX123");
  }

  // -------------------------------
  // getStatusOfExport
  // -------------------------------

  @Test
  void testGetStatusOfExport_success() {
    stubFor(
        get(urlEqualTo("/excerpts/EX123/status")).willReturn(okJson("{\"status\": \"READY\"}")));

    String status = api.getStatusOfExport("EX123");

    assertThat(status).isEqualTo("READY");
  }

  // -------------------------------
  // getExport (default PDF)
  // -------------------------------

  @Test
  void testGetExport_default_success() throws Exception {
    byte[] pdfMock = "PDF_CONTENT".getBytes();

    stubFor(
        get(urlEqualTo("/excerpts/EX555"))
            .willReturn(aResponse().withStatus(200).withBody(pdfMock)));

    File file = api.getExport("EX555");

    assertThat(file).exists();
    assertThat(Files.readAllBytes(file.toPath())).isEqualTo(pdfMock);
  }

  // -------------------------------
  // getExport with format
  // -------------------------------

  @Test
  void testGetExport_withFormat_success() throws Exception {
    byte[] bytes = "HELLO_DATA".getBytes();

    stubFor(
        get(urlEqualTo("/excerpts/EX777")).willReturn(aResponse().withStatus(200).withBody(bytes)));

    File file = api.getExport("EX777", ExcerptFormats.PDF);

    assertThat(file).exists();
    assertThat(file.getName()).endsWith(".pdf");
    assertThat(Files.readAllBytes(file.toPath())).isEqualTo(bytes);
  }

  // -------------------------------
  // tryToCreateWithPayload
  // -------------------------------

  @Test
  void testTryToCreateWithPayload_success() {
    stubFor(post(urlEqualTo("/excerpts")).willReturn(okJson("{\"result\": \"OK\"}")));

    ExportRequest req = new ExportRequest();

    var resp = api.tryToCreateWithPayload(req);

    assertThat(resp.statusCode()).isEqualTo(200);
    assertThat(resp.jsonPath().getString("result")).isEqualTo("OK");
  }
}
