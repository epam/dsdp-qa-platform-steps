package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.response.Translation;

class MasterRegistryTranslationsApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private MasterRegistryTranslationsApiSteps steps;

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
    steps = new MasterRegistryTranslationsApiSteps(service);
  }

  // ============================================================================
  // GET TRANSLATION LIST — SUCCESS
  // ============================================================================
  @Test
  void testGetTranslationsList_success() throws Exception {
    List<Translation> list = List.of(new Translation("en"));

    stubFor(
        get(urlEqualTo("/versions/master/i18n"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<Translation> resp = steps.getTranslationsList();

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getName()).isEqualTo("en");
  }

  // ============================================================================
  // GET TRANSLATION CONTENT — SUCCESS
  // ============================================================================
  @Test
  void testGetTranslationContent_success() {
    String json = "{ \"hello\": \"world\" }";

    stubFor(
        get(urlEqualTo("/versions/master/i18n/en"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)));

    String resp = steps.getTranslationContent("en");

    assertThat(resp).isEqualTo(json);
  }

  // ============================================================================
  // CREATE TRANSLATION — SUCCESS (201)
  // ============================================================================
  @Test
  void testCreateTranslation_success() {
    stubFor(
        post(urlEqualTo("/versions/master/i18n/en"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CREATED)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"result\":\"created\"}")));

    String result = steps.createTranslation("{data}", "en");

    assertThat(result).contains("created");
  }

  // ============================================================================
  // UPDATE TRANSLATION — SUCCESS (200)
  // ============================================================================
  @Test
  void testUpdateTranslation_success() {
    stubFor(
        put(urlEqualTo("/versions/master/i18n/en"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"updated\":true}")));

    String resp = steps.updateTranslation("{data}", "en", Map.of("X-Test", "1"));

    assertThat(resp).contains("updated");
  }

  // ============================================================================
  // DELETE TRANSLATION — SUCCESS (204)
  // ============================================================================
  @Test
  void testDeleteTranslation_success() {
    stubFor(
        delete(urlEqualTo("/versions/master/i18n/en"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

    steps.deleteTranslation("en", Map.of("Header", "v"));

    wireMock.verify(deleteRequestedFor(urlEqualTo("/versions/master/i18n/en")));
  }
}
