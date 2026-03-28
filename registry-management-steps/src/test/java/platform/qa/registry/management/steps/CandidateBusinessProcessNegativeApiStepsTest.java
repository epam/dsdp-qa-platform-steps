package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.response.ErrorBody;
import platform.qa.registry.management.dto.response.ErrorResponse;

class CandidateBusinessProcessNegativeApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private CandidateBusinessProcessNegativeApiSteps steps;

  @BeforeAll
  static void start() {
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
    Service service =
        new Service("http://localhost:" + wireMock.port(), new User("usr", "token123"));
    steps = new CandidateBusinessProcessNegativeApiSteps(service);
  }

  // ----------------------------------------------------------
  // Helper: JSON Error
  // ----------------------------------------------------------
  private String jsonError(String msg) throws Exception {
    return mapper.writeValueAsString(
        ErrorResponse.builder()
            .error(
                ErrorBody.builder()
                    .message(msg)
                    .code("ERR")
                    .traceId("123")
                    .details("details")
                    .build())
            .build());
  }

  // ----------------------------------------------------------
  // Unauthorized GET
  // ----------------------------------------------------------
  @Test
  void testGetBpContentUnauthorized() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonError("unauthorized"))));

    ErrorResponse resp = steps.getBpContentUnauthorized("v1", "bp1");

    assertThat(resp.getError().getMessage()).isEqualTo("unauthorized");
  }

  // ----------------------------------------------------------
  // Method Not Allowed (POST instead of GET)
  // ----------------------------------------------------------
  @Test
  void testGetBpContentNotMethodAllowed() throws Exception {
    stubFor(
        post(urlEqualTo("/versions/candidates/v1/business-processes"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_METHOD_NOT_ALLOWED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonError("method not allowed"))));

    ErrorResponse resp = steps.getBpContentNotMethodAllowed("v1", "bp1");

    assertThat(resp.getError().getMessage()).isEqualTo("method not allowed");
  }

  // ----------------------------------------------------------
  // Not Found
  // ----------------------------------------------------------
  @Test
  void testGetBpContentNotFound() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_NOT_FOUND)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonError("not found"))));

    ErrorResponse resp = steps.getBpContentNotFound("v1", "bp1");

    assertThat(resp.getError().getMessage()).isEqualTo("not found");
  }

  // ----------------------------------------------------------
  // Unauthorized CREATE
  // ----------------------------------------------------------
  @Test
  void testCreateBpContentUnauthorized() throws Exception {
    stubFor(
        post(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonError("create unauthorized"))));

    ErrorResponse resp = steps.createBpContentUnauthorized("v1", "<xml/>", "bp1");

    assertThat(resp.getError().getMessage()).isEqualTo("create unauthorized");
  }

  // ----------------------------------------------------------
  // Unauthorized UPDATE
  // ----------------------------------------------------------
  @Test
  void testUpdateBpContentUnauthorized() throws Exception {
    stubFor(
        put(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonError("update unauthorized"))));

    ErrorResponse resp = steps.updateBpContentUnauthorized("v1", "<xml/>", "bp1");

    assertThat(resp.getError().getMessage()).isEqualTo("update unauthorized");
  }

  // ----------------------------------------------------------
  // Conflict UPDATE
  // ----------------------------------------------------------
  @Test
  void testUpdateBpContentConflict() throws Exception {
    stubFor(
        put(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CONFLICT)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonError("conflict update"))));

    ErrorResponse resp = steps.updateBpContentConflict("v1", "<xml/>", "bp1");

    assertThat(resp.getError().getMessage()).isEqualTo("conflict update");
  }

  // ----------------------------------------------------------
  // Unauthorized DELETE
  // ----------------------------------------------------------
  @Test
  void testDeleteBpContentUnauthorized() {
    stubFor(
        delete(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")));

    steps.deleteBpContentUnauthorized("v1", "bp1");
  }

  // ----------------------------------------------------------
  // Not Found DELETE
  // ----------------------------------------------------------
  @Test
  void testDeleteBpContentNotFound() {
    stubFor(
        delete(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_NOT_FOUND)
                    .withHeader("Content-Type", "application/json")));

    steps.deleteBpContentNotFound("v1", "bp1");
  }

  // ----------------------------------------------------------
  // Invalid XML → Unprocessable Entity
  // ----------------------------------------------------------
  @Test
  void testCreateBusinessProcessWithInvalidXml() {
    stubFor(
        post(urlEqualTo("/versions/candidates/v1/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                    .withHeader("Content-Type", "application/json")));

    steps.createBusinessProcessWithInvalidXml("v1", "{ bad xml }", "bp1");
  }
}
