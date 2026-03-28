package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.http.ContentType;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.response.EntityInfo;

class CandidateBusinessProcessApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private CandidateBusinessProcessApiSteps steps;

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
    steps = new CandidateBusinessProcessApiSteps(svc);
  }

  // ========================================================================
  // GET LIST — SUCCESS
  // ========================================================================
  @Test
  void testGetBpList_success() throws Exception {
    List<EntityInfo> list = List.of(new EntityInfo());

    stubFor(
        get(urlEqualTo("/versions/candidates/v2/business-processes"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<EntityInfo> response = steps.getBpList("v2");

    assertThat(response).hasSize(1);
  }

  // ========================================================================
  // GET LIST — ERROR
  // ========================================================================
  @Test
  void testGetBpList_notFound() {
    stubFor(
        get(urlEqualTo("/versions/candidates/v2/business-processes"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NOT_FOUND)));

    assertThrows(AssertionError.class, () -> steps.getBpList("v2"));
  }

  // ========================================================================
  // GET CONTENT — SUCCESS
  // ========================================================================
  @Test
  void testGetBusinessProcessContent_success() {
    stubFor(
        get(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", ContentType.XML.toString())
                    .withBody("<xml>OK</xml>")));

    String result = steps.getBusinessProcessContent("v1", "bp1");

    assertThat(result).contains("xml");
  }

  // ========================================================================
  // GET CONTENT — BAD REQUEST
  // ========================================================================
  @Test
  void testGetBusinessProcessContent_badRequest() {
    stubFor(
        get(urlEqualTo("/versions/candidates/v1/business-process/bp1"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));

    assertThrows(AssertionError.class, () -> steps.getBusinessProcessContent("v1", "bp1"));
  }

  // ========================================================================
  // CREATE — SUCCESS (201)
  // ========================================================================
  @Test
  void testCreateBusinessProcess_success() {
    stubFor(
        post(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CREATED)
                    .withHeader("Content-Type", ContentType.XML.toString())
                    .withBody("<ok/>")));

    String response = steps.createBusinessProcess("v1", "<bpmn/>", "bp1");

    assertThat(response).contains("ok");
  }

  // ========================================================================
  // CREATE — CONFLICT
  // ========================================================================
  @Test
  void testCreateBusinessProcess_conflict() {
    stubFor(
        post(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_CONFLICT)));

    assertThrows(AssertionError.class, () -> steps.createBusinessProcess("v1", "<bpmn/>", "bp1"));
  }

  // ========================================================================
  // UPDATE — SUCCESS
  // ========================================================================
  @Test
  void testUpdateBusinessProcess_success() {
    stubFor(
        put(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", ContentType.XML.toString())
                    .withBody("<updated/>")));

    String result = steps.updateBusinessProcess("v1", "<bpmn/>", "bp1");

    assertThat(result).contains("updated");
  }

  // ========================================================================
  // UPDATE — FAIL
  // ========================================================================
  @Test
  void testUpdateBusinessProcess_fail() {
    stubFor(
        put(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));

    assertThrows(AssertionError.class, () -> steps.updateBusinessProcess("v1", "<bpmn/>", "bp1"));
  }

  // ========================================================================
  // DELETE — SUCCESS (204)
  // ========================================================================
  @Test
  void testDeleteBusinessProcess_success() {
    stubFor(
        delete(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

    steps.deleteBusinessProcess("v1", "bp1");

    wireMock.verify(
        deleteRequestedFor(urlEqualTo("/versions/candidates/v1/business-processes/bp1")));
  }

  // ========================================================================
  // DELETE — FAILURE
  // ========================================================================
  @Test
  void testDeleteBusinessProcess_fail() {
    stubFor(
        delete(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NOT_FOUND)));

    assertThrows(AssertionError.class, () -> steps.deleteBusinessProcess("v1", "bp1"));
  }
}
