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

class MasterFormNegativeApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private MasterFormNegativeApiSteps steps;

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
    steps = new MasterFormNegativeApiSteps(service);
  }

  // ----------------------------------------------------------------------
  // Helper JSON Builders
  // ----------------------------------------------------------------------

  private String jsonErrorResponse(String msg) throws Exception {
    return mapper.writeValueAsString(
        ErrorResponse.builder()
            .error(
                ErrorBody.builder()
                    .traceId("123")
                    .message(msg)
                    .code("ERR")
                    .details("details")
                    .build())
            .build());
  }

  private String jsonErrorBody() throws Exception {
    return mapper.writeValueAsString(
        ErrorBody.builder().traceId("321").code("NOT_FOUND").details("missing form").build());
  }

  // ----------------------------------------------------------------------
  // 401 — Unauthorized (GET content)
  // ----------------------------------------------------------------------
  @Test
  void testGetMasterFormUnauthorized() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/master/forms/formA"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonErrorResponse("unauthorized"))));

    ErrorResponse resp = steps.getMasterFormUnauthorized("formA");

    assertThat(resp.getError().getMessage()).isEqualTo("unauthorized");
    assertThat(resp.getError().getCode()).isEqualTo("ERR");
  }

  // ----------------------------------------------------------------------
  // 404 — Not Found (returns ErrorBody, NOT ErrorResponse)
  // ----------------------------------------------------------------------
  @Test
  void testGetMasterFormNotFound() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/master/forms/form404"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_NOT_FOUND)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonErrorBody())));

    ErrorBody body = steps.getMasterFormNotFound("form404");

    assertThat(body.getCode()).isEqualTo("NOT_FOUND");
    assertThat(body.getDetails()).isEqualTo("missing form");
  }

  // ----------------------------------------------------------------------
  // 401 — Unauthorized on GET form list
  // ----------------------------------------------------------------------
  @Test
  void testGetMasterFormsUnauthorized() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/master/forms"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonErrorResponse("unauthorized list"))));

    ErrorResponse resp = steps.getMasterFormsUnauthorized();

    assertThat(resp.getError().getMessage()).isEqualTo("unauthorized list");
  }

  // ----------------------------------------------------------------------
  // 401 — Unauthorized POST for form creation
  // ----------------------------------------------------------------------
  @Test
  void testPostMasterFormUnauthorized() throws Exception {
    stubFor(
        post(urlEqualTo("/versions/master/forms/formA"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonErrorResponse("unauthorized create"))));

    ErrorResponse resp = steps.postMasterFormUnauthorized("formA");

    assertThat(resp.getError().getMessage()).isEqualTo("unauthorized create");

    wireMock.verify(postRequestedFor(urlEqualTo("/versions/master/forms/formA")));
  }
}
