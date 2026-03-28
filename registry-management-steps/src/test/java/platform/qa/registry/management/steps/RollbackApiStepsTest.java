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

public class RollbackApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private RollbackApiSteps steps;

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
    Service svc = new Service("http://localhost:" + wireMock.port(), new User("usr", "token"));
    steps = new RollbackApiSteps(svc);
  }

  private String json(String msg) throws Exception {
    return mapper.writeValueAsString(msg);
  }

  // =====================================================================
  // BUSINESS PROCESS ROLLBACK — SUCCESS
  // =====================================================================
  @Test
  void testRollbackBusinessProcess_success() throws Exception {

    stubFor(
        post(urlEqualTo("/versions/candidates/123/business-processes/bpA/rollback"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json("bp rollback OK"))));

    String result = steps.rollbackBusinessProcess("bpA", "123");

    assertThat(result).contains("bp rollback OK");

    wireMock.verify(
        postRequestedFor(urlEqualTo("/versions/candidates/123/business-processes/bpA/rollback"))
            .withHeader("Content-Type", containing("application/json")));
  }

  // =====================================================================
  // FORM ROLLBACK — SUCCESS
  // =====================================================================
  @Test
  void testRollbackForm_success() throws Exception {

    stubFor(
        post(urlEqualTo("/versions/candidates/123/forms/formA/rollback"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json("form rollback OK"))));

    String result = steps.rollbackForm("formA", "123");

    assertThat(result).contains("form rollback OK");
  }

  // =====================================================================
  // BUSINESS PROCESS GROUPING ROLLBACK — SUCCESS
  // =====================================================================
  @Test
  void testRollbackBpGrouping_success() throws Exception {

    stubFor(
        post(urlEqualTo("/versions/candidates/123/business-process-groups/rollback"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json("grouping rollback OK"))));

    String result = steps.rollbackBpGrouping("123");

    assertThat(result).contains("grouping rollback OK");
  }

  // =====================================================================
  // DATA MODEL ROLLBACK — SUCCESS
  // =====================================================================
  @Test
  void testRollbackDataModel_success() throws Exception {

    stubFor(
        post(urlEqualTo("/versions/candidates/123/data-model/tables/rollback"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json("data model rollback OK"))));

    String result = steps.rollbackDataModel("123");

    assertThat(result).contains("data model rollback OK");
  }

  // =====================================================================
  // TRANSLATION ROLLBACK — SUCCESS
  // =====================================================================
  @Test
  void testRollbackTranslation_success() throws Exception {

    stubFor(
        post(urlEqualTo("/versions/candidates/123/i18n/ua/rollback"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json("translation rollback OK"))));

    String result = steps.rollbackTranslation("ua", "123");

    assertThat(result).contains("translation rollback OK");
  }
}
