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

class MasterGlobalSettingsApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private MasterGlobalSettingsApiSteps steps;

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
    steps = new MasterGlobalSettingsApiSteps(service);
  }

  // -------------------------------------------------------------
  // Helper for JSON error
  // -------------------------------------------------------------
  private String jsonError(String msg) throws Exception {
    return mapper.writeValueAsString(
        ErrorResponse.builder()
            .error(
                ErrorBody.builder()
                    .message(msg)
                    .traceId("123")
                    .code("ERR")
                    .details("details")
                    .build())
            .build());
  }

  // =============================================================
  // SUCCESS GET
  // =============================================================
  @Test
  void testGetGlobalSettings_success() throws Exception {
    GlobalSettings settings =
        GlobalSettings.builder()
            .title("MasterTitle")
            .titleFull("MasterFullTitle")
            .theme("dark")
            .supportEmail("help@master.com")
            .supportChannelUrl("https://master.chat")
            .build();

    stubFor(
        get(urlEqualTo("/versions/master/settings"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(settings))));

    GlobalSettings resp = steps.getGlobalSettings();

    assertThat(resp.getTitle()).isEqualTo("MasterTitle");
    assertThat(resp.getTheme()).isEqualTo("dark");
  }

  // =============================================================
  // UNAUTHORIZED GET
  // =============================================================
  @Test
  void testGetMasterGlobalSettingsUnauthorized() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/master/settings"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonError("unauthorized"))));

    ErrorResponse resp = steps.getMasterGlobalSettingsUnauthorized();

    assertThat(resp.getError().getMessage()).isEqualTo("unauthorized");
  }

  // =============================================================
  // INVALID JSON
  // =============================================================
  @Test
  void testGetGlobalSettings_invalidJson() {
    stubFor(
        get(urlEqualTo("/versions/master/settings"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{ invalid json")));

    assertThrows(Exception.class, steps::getGlobalSettings);
  }

  // =============================================================
  // NOT FOUND → must throw AssertionError
  // =============================================================
  @Test
  void testGetGlobalSettings_notFound() {
    stubFor(
        get(urlEqualTo("/versions/master/settings"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NOT_FOUND)));

    assertThrows(AssertionError.class, steps::getGlobalSettings);
  }
}
