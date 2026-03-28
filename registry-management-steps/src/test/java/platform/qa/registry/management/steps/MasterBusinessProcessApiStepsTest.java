package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.response.EntityInfo;

class MasterBusinessProcessApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private MasterBusinessProcessApiSteps steps;

  @BeforeAll
  static void startServer() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());
  }

  @AfterAll
  static void stop() {
    wireMock.stop();
  }

  @BeforeEach
  void setup() {
    Service svc = new Service("http://localhost:" + wireMock.port(), new User("u", "token"));
    steps = new MasterBusinessProcessApiSteps(svc);
  }

  // ========================================================================
  // GET LIST — SUCCESS
  // ========================================================================
  @Test
  void testGetBusinessProcesses_success() throws Exception {
    List<EntityInfo> list = List.of(new EntityInfo());

    stubFor(
        get(urlEqualTo("/versions/master/business-processes"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<EntityInfo> response = steps.getBusinessProcesses();

    assertThat(response).hasSize(1);
  }

  // ========================================================================
  // GET LIST — FAIL
  // ========================================================================
  @Test
  void testGetBusinessProcesses_fail() {
    stubFor(
        get(urlEqualTo("/versions/master/business-processes"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NOT_FOUND)));

    assertThrows(AssertionError.class, () -> steps.getBusinessProcesses());
  }

  // ========================================================================
  // GET CONTENT — SUCCESS
  // ========================================================================
  @Test
  void testGetBusinessProcessContent_success() {
    stubFor(
        get(urlEqualTo("/versions/master/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", ContentType.XML.toString())
                    .withBody("<xml>MASTER OK</xml>")));

    String result = steps.getBusinessProcessContent("bp1");

    assertThat(result).contains("MASTER OK");
  }

  // ========================================================================
  // GET CONTENT — FAIL
  // ========================================================================
  @Test
  void testGetBusinessProcessContent_fail() {
    stubFor(
        get(urlEqualTo("/versions/master/business-processes/bp1"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));

    assertThrows(AssertionError.class, () -> steps.getBusinessProcessContent("bp1"));
  }

  // ========================================================================
  // CREATE — SUCCESS
  // ========================================================================
  @Test
  void testCreateBpInMasterVersion_success() {
    stubFor(
        post(urlEqualTo("/versions/master/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CREATED)
                    .withHeader("Content-Type", ContentType.XML.toString())
                    .withBody("<created/>")));

    String resp = steps.createBpInMasterVersion("<bpmn/>", "bp1");

    assertThat(resp).contains("created");
  }

  // ========================================================================
  // CREATE — FAIL
  // ========================================================================
  @Test
  void testCreateBpInMasterVersion_fail() {
    stubFor(
        post(urlEqualTo("/versions/master/business-processes/bp1"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_CONFLICT)));

    assertThrows(AssertionError.class, () -> steps.createBpInMasterVersion("<bpmn/>", "bp1"));
  }

  // ========================================================================
  // UPDATE — SUCCESS
  // ========================================================================
  @Test
  void testUpdateBpInMasterVersion_success() {
    Map<String, String> headers = new HashMap<>();
    headers.put("X-Test", "1");

    stubFor(
        put(urlEqualTo("/versions/master/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", ContentType.XML.toString())
                    .withBody("<updated/>")));

    String result = steps.updateBpInMasterVersion("<xml/>", "bp1", headers);

    assertThat(result).contains("updated");
  }

  // ========================================================================
  // UPDATE — FAIL
  // ========================================================================
  @Test
  void testUpdateBpInMasterVersion_fail() {
    Map<String, String> headers = Map.of("X", "Y");

    stubFor(
        put(urlEqualTo("/versions/master/business-processes/bp1"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));

    assertThrows(
        AssertionError.class, () -> steps.updateBpInMasterVersion("<xml/>", "bp1", headers));
  }

  // ========================================================================
  // DELETE — SUCCESS
  // ========================================================================
  @Test
  void testDeleteBusinessProcess_success() {
    Map<String, String> headers = Map.of("A", "B");

    stubFor(
        delete(urlEqualTo("/versions/master/business-processes/bp1"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

    steps.deleteBusinessProcess("bp1", headers);

    wireMock.verify(deleteRequestedFor(urlEqualTo("/versions/master/business-processes/bp1")));
  }

  // ========================================================================
  // DELETE — FAIL
  // ========================================================================
  @Test
  void testDeleteBusinessProcess_fail() {
    Map<String, String> headers = Map.of("A", "B");

    stubFor(
        delete(urlEqualTo("/versions/master/business-processes/bp1"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NOT_FOUND)));

    assertThrows(AssertionError.class, () -> steps.deleteBusinessProcess("bp1", headers));
  }
}
