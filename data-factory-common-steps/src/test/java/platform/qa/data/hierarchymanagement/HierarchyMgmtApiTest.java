package platform.qa.data.hierarchymanagement;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.hierarchymanagement.DocumentRegistration;
import platform.qa.pojo.hierarchymanagement.Unit;

class HierarchyMgmtApiTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private static MockedConstruction<SignatureSteps> signatureMockConstruction;
  private HierarchyMgmtApi api;
  private SignatureSteps signatureMock;
  private Service dataFactory;

  @BeforeAll
  static void beforeAll() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());

    signatureMockConstruction =
        Mockito.mockConstruction(
            SignatureSteps.class,
            (mock, context) -> {
              Mockito.when(mock.signRequest(any())).thenReturn("SIG-ID");
              Mockito.when(mock.signDeleteRequest(any())).thenReturn("DEL-ID");
            });
  }

  @AfterAll
  static void afterAll() {
    wireMock.stop();
    signatureMockConstruction.close();
  }

  @BeforeEach
  void setup() throws Exception {

    User user = new User("usr", "tkn");
    user.setToken("token");
    dataFactory = new Service("http://localhost:" + wireMock.port(), user);

    api = new HierarchyMgmtApi(dataFactory, dataFactory, List.of());

    signatureMock = signatureMockConstruction.constructed().get(0);

    Field f = HierarchyMgmtApi.class.getDeclaredField("signatureSteps");
    f.setAccessible(true);
    f.set(api, signatureMock);
  }

  @Test
  void testGetUnit_success() throws Exception {
    Unit u = new Unit();
    u.setId("U1");

    stubFor(
        get(urlEqualTo("/unit/U1"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(mapper.writeValueAsString(u))
                    .withHeader("Content-Type", "application/json")));

    Unit resp = api.getUnit("U1");

    assertThat(resp.getId()).isEqualTo("U1");
  }

  @Test
  void testCreateUnit_success() {
    Unit u = new Unit();

    stubFor(
        post(urlEqualTo("/unit/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withBody("{\"id\":\"SIG-ID\"}")
                    .withHeader("Content-Type", "application/json")));

    String id = api.createUnit(u);

    assertThat(id).isEqualTo("SIG-ID");
    Mockito.verify(signatureMock).signRequest(u);
  }

  @Test
  void testDeleteUnit_success() {
    stubFor(delete(urlEqualTo("/unit/U5")).willReturn(aResponse().withStatus(204)));

    api.deleteUnit("U5");

    Mockito.verify(signatureMock).signDeleteRequest("U5");
  }

  @Test
  void testGetDocumentRegistration_success() throws Exception {
    DocumentRegistration doc = new DocumentRegistration();
    doc.setId("DOC7");

    stubFor(
        get(urlEqualTo("/document-registration/DOC7"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(mapper.writeValueAsString(doc))
                    .withHeader("Content-Type", "application/json")));

    DocumentRegistration resp = api.getDocumentRegistration("DOC7");

    assertThat(resp.getId()).isEqualTo("DOC7");
  }

  @Test
  void testCreateDocumentRegistration_success() {
    DocumentRegistration doc = new DocumentRegistration();

    stubFor(
        post(urlEqualTo("/document-registration/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withBody("{\"id\":\"SIG-ID\"}")
                    .withHeader("Content-Type", "application/json")));

    String id = api.createDocumentRegistration(doc);

    assertThat(id).isEqualTo("SIG-ID");
    Mockito.verify(signatureMock).signRequest(doc);
  }

  @Test
  void testDeleteDocumentRegistration_success() {
    stubFor(
        delete(urlEqualTo("/document-registration/D1")).willReturn(aResponse().withStatus(204)));

    api.deleteDocumentRegistration("D1");

    Mockito.verify(signatureMock).signDeleteRequest("D1");
  }
}
