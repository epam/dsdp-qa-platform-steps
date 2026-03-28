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

class CandidateRegistryTranslationsApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private CandidateRegistryTranslationsApiSteps steps;

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
    Service service =
        new Service("http://localhost:" + wireMock.port(), new User("usr", "token123"));
    steps = new CandidateRegistryTranslationsApiSteps(service);
  }

  // ============================================================================
  // GET TRANSLATION CONTENT — SUCCESS
  // ============================================================================
  @Test
  void testGetTranslationContent_success() {
    String json = "{ \"key\": \"value\" }";

    stubFor(
        get(urlEqualTo("/versions/candidates/123/i18n/en"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)));

    String response = steps.getTranslationContent("123", "en");

    assertThat(response).isEqualTo(json);
  }

  // ============================================================================
  // GET TRANSLATIONS LIST — SUCCESS
  // ============================================================================
  @Test
  void testGetTranslationsList_success() throws Exception {
    List<Translation> list = List.of(new Translation("en"));

    stubFor(
        get(urlEqualTo("/versions/candidates/123/i18n"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<Translation> resp = steps.getTranslationsList("123");

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getName()).isEqualTo("en");
  }

  // ============================================================================
  // CREATE TRANSLATION — SUCCESS (201)
  // ============================================================================
  @Test
  void testCreateTranslation_success() {
    stubFor(
        post(urlEqualTo("/versions/candidates/123/i18n/en"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CREATED)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"result\":\"created\"}")));

    String result = steps.createTranslation("123", "en", "{data}");

    assertThat(result).contains("created");
  }

  // ============================================================================
  // UPDATE TRANSLATION — SUCCESS (200)
  // ============================================================================
  @Test
  void testUpdateTranslation_success() {
    stubFor(
        put(urlEqualTo("/versions/candidates/123/i18n/en"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"result\":\"updated\"}")));

    String result = steps.updateTranslation("123", "en", "{data}", Map.of("X-Test", "1"));

    assertThat(result).contains("updated");
  }

  // ============================================================================
  // DELETE TRANSLATION — SUCCESS (204)
  // ============================================================================
  @Test
  void testDeleteTranslation_success() {
    stubFor(
        delete(urlEqualTo("/versions/candidates/123/i18n/en"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

    steps.deleteTranslation("123", "en", Map.of("Header", "value"));

    wireMock.verify(deleteRequestedFor(urlEqualTo("/versions/candidates/123/i18n/en")));
  }
}
