package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.response.ErrorBody;
import platform.qa.registry.management.dto.response.ErrorResponse;

class MasterBusinessProcessNegativeApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private MasterBusinessProcessNegativeApiSteps steps;

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
  void init() {
    Service service =
        new Service("http://localhost:" + wireMock.port(), new User("usr", "token123"));
    steps = new MasterBusinessProcessNegativeApiSteps(service);
  }

  // --------------------------------------------------------
  // Helper: JSON error builder
  // --------------------------------------------------------
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

  // --------------------------------------------------------
  // Unauthorized GET
  // --------------------------------------------------------
  @Test
  void testGetBusinessProcessContentUnauthorized() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/master/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonError("unauthorized"))));

    ErrorResponse resp = steps.getBusinessProcessContentUnauthorized("bp1");

    assertThat(resp.getError().getMessage()).isEqualTo("unauthorized");
  }

  // --------------------------------------------------------
  // Not Found GET
  // --------------------------------------------------------
  @Test
  void testGetBusinessProcessContentNotFound() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/master/business-processes/bp1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_NOT_FOUND)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonError("not found"))));

    ErrorResponse resp = steps.getBusinessProcessContentNotFound("bp1");

    assertThat(resp.getError().getMessage()).isEqualTo("not found");
  }

  // --------------------------------------------------------
  // Unauthorized LIST GET
  // --------------------------------------------------------
  @Test
  void testGetListBusinessProcessContentUnauthorized() throws Exception {
    List<ErrorResponse> errors =
        List.of(
            ErrorResponse.builder()
                .error(
                    ErrorBody.builder()
                        .message("unauthorized list")
                        .code("ERR")
                        .traceId("123")
                        .details("details")
                        .build())
                .build());

    stubFor(
        get(urlEqualTo("/versions/master/business-processes"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(errors))));

    List<ErrorResponse> resp = steps.getListBusinessProcessContentUnauthorized();

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getError().getMessage()).isEqualTo("unauthorized list");
  }
}
