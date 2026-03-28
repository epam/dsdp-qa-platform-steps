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
import platform.qa.registry.management.dto.request.form.Form;
import platform.qa.registry.management.dto.response.EntityInfo;

class MasterFormsApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;

  private MasterFormsApiSteps steps;

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
    steps = new MasterFormsApiSteps(service);
  }

  // ======================================================================
  // GET FORM LIST (200)
  // ======================================================================
  @Test
  void testGetFormListFromMaster_success() throws Exception {

    EntityInfo e = new EntityInfo();
    e.setName("masterForm");

    List<EntityInfo> list = List.of(e);

    stubFor(
        get(urlEqualTo("/versions/master/forms"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<EntityInfo> result = steps.getFormListFromMasterVersion();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("masterForm");
  }

  // ======================================================================
  // GET FORM CONTENT (200)
  // ======================================================================
  @Test
  void testGetFormContentFromMaster_success() {

    String json = "{\"field\": \"value\"}";

    stubFor(
        get(urlEqualTo("/versions/master/forms/myForm"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)));

    String result = steps.getFormContentFromMasterVersion("myForm");

    assertThat(result).contains("value");
  }

  // ======================================================================
  // CREATE FORM (201)
  // ======================================================================
  @Test
  void testCreateFormInMaster_success() {

    Form form = new Form();
    form.setName("newMasterForm");
    form.setTitle("My Title");

    stubFor(
        post(urlEqualTo("/versions/master/forms/newMasterForm"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CREATED)
                    .withHeader("Content-Type", "application/json")
                    .withBody("\"CREATED\"")));

    String response = steps.createFormInMasterVersion(form);

    assertThat(response).contains("CREATED");
  }

  // ======================================================================
  // UPDATE FORM (200)
  // ======================================================================
  @Test
  void testUpdateFormInMaster_success() {

    Form form = new Form();
    form.setName("updateForm");
    form.setTitle("Updated");

    stubFor(
        put(urlEqualTo("/versions/master/forms/updateForm"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody("\"UPDATED\"")));

    String response = steps.updateFormInMasterVersion(form, Map.of("h", "v"));

    assertThat(response).contains("UPDATED");
  }

  // ======================================================================
  // DELETE FORM (204)
  // ======================================================================
  @Test
  void testDeleteFormInMaster_success() {

    stubFor(
        delete(urlEqualTo("/versions/master/forms/toDelete"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

    steps.deleteFormInMasterVersion("toDelete", Map.of("h", "v"));
  }
}
