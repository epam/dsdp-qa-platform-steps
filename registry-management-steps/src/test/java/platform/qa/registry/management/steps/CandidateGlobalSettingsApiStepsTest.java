package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.response.ErrorBody;
import platform.qa.registry.management.dto.response.ErrorResponse;
import platform.qa.registry.management.dto.response.GlobalSettings;

class CandidateGlobalSettingsApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private CandidateGlobalSettingsApiSteps steps;

  @BeforeAll
  static void startServer() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());
  }

  @AfterAll
  static void stopServer() {
    wireMock.stop();
  }

  @BeforeEach
  void setup() {
    Service service =
        new Service("http://localhost:" + wireMock.port(), new User("usr", "token123"));
    steps = new CandidateGlobalSettingsApiSteps(service);
  }

  // -------------------------------------------------------------
  // Helper to generate JSON error
  // -------------------------------------------------------------
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

  // =============================================================
  // GET SUCCESS
  // =============================================================
  @Test
  void testGetGlobalSettings_success() throws Exception {
    GlobalSettings settings =
        GlobalSettings.builder()
            .title("MyTitle")
            .titleFull("FullTitle")
            .theme("dark")
            .supportEmail("help@test.com")
            .supportChannelUrl("https://chat.example")
            .build();

    stubFor(
        get(urlEqualTo("/versions/candidates/v1/settings"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(settings))));

    GlobalSettings resp = steps.getGlobalSettings("v1");

    assertThat(resp.getTitle()).isEqualTo("MyTitle");
    assertThat(resp.getTheme()).isEqualTo("dark");
  }

  // =============================================================
  // GET UNAUTHORIZED
  // =============================================================
  @Test
  void testGetGlobalSettings_unauthorized() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/candidates/v1/settings"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonError("unauthorized"))));

    ErrorResponse resp = steps.getGlobalSettingsUnauthorized("v1");

    assertThat(resp.getError().getMessage()).isEqualTo("unauthorized");
  }

  // =============================================================
  // GET INVALID JSON → Should throw exception
  // =============================================================
  @Test
  void testGetGlobalSettings_invalidJson() {
    stubFor(
        get(urlEqualTo("/versions/candidates/v1/settings"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{ invalid json")));

    assertThrows(Exception.class, () -> steps.getGlobalSettings("v1"));
  }

  // =============================================================
  // GET NOT FOUND → Should fail positiveRequest
  // =============================================================
  @Test
  void testGetGlobalSettings_notFound() {
    stubFor(
        get(urlEqualTo("/versions/candidates/v1/settings"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NOT_FOUND)));

    assertThrows(AssertionError.class, () -> steps.getGlobalSettings("v1"));
  }

  // =============================================================
  // UPDATE SUCCESS
  // =============================================================
  @Test
  void testUpdateGlobalSettings_success() throws Exception {
    GlobalSettings request =
        GlobalSettings.builder()
            .title("NewTitle")
            .titleFull("NewFullTitle")
            .theme("light")
            .supportEmail("new@test.com")
            .supportChannelUrl("https://new.example")
            .build();

    stubFor(
        put(urlEqualTo("/versions/candidates/v1/settings"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(request))));

    GlobalSettings resp = steps.updateGlobalSettings("v1", request);

    assertThat(resp.getTitle()).isEqualTo("NewTitle");
    assertThat(resp.getTheme()).isEqualTo("light");
  }

  // =============================================================
  // UPDATE FAIL (BAD REQUEST)
  // =============================================================
  @Test
  void testUpdateGlobalSettings_fail() {
    stubFor(
        put(urlEqualTo("/versions/candidates/v1/settings"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));

    assertThrows(
        AssertionError.class,
        () -> steps.updateGlobalSettings("v1", new GlobalSettings("t", "tf", "dark", "a", "b")));
  }
}
