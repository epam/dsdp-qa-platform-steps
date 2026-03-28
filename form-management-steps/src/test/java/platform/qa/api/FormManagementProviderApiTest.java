package platform.qa.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.entity.CreatedFormResponse;

class FormManagementProviderApiTest {

  private WireMockServer wireMock;
  private FormManagementProviderApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("user", "pwd");
    user.setToken("TEST_TOKEN");
    Service service = new Service("http://localhost:" + wireMock.port(), user);

    api = new FormManagementProviderApi(service);

    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  @Test
  void testGetAllForms_success() {
    stubFor(
        get(urlEqualTo("/form?type=form&limit=20000"))
            .willReturn(
                okJson(
                    """
                [
                    {"_id":"1","name":"AUTO_Form"},
                    {"_id":"2","name":"Other"}
                ]
            """)));

    List<Map> forms = api.getAllForms();
    assertThat(forms).hasSize(2);
    assertThat(forms.get(0).get("name")).isEqualTo("AUTO_Form");
  }

  @Test
  void testGetFormById_success() {
    stubFor(
        get(urlEqualTo("/form/FORM123"))
            .willReturn(
                okJson(
                    """
                {"_id":"FORM123","name":"My Form"}
            """)));

    Map form = api.getFormById("FORM123");
    assertThat(form.get("_id")).isEqualTo("FORM123");
    assertThat(form.get("name")).isEqualTo("My Form");
  }

  @Test
  void testGetFormByName_success() {
    stubFor(
        get(urlEqualTo("/form?name=TestForm"))
            .willReturn(
                okJson(
                    """
                                [
                                    {"_id":"F1","name":"TestForm"}
                                ]
                            """)));

    List<HashMap> forms = api.getFormByName("TestForm");
    assertThat(forms).hasSize(1);
    assertThat(forms.get(0).get("_id")).isEqualTo("F1");
  }

  @Test
  void testDeleteFormById_success() {
    stubFor(delete(urlEqualTo("/form/DEL123")).willReturn(ok()));

    api.deleteFormById("DEL123");

    verify(deleteRequestedFor(urlEqualTo("/form/DEL123")));
  }

  @Test
  void testDeleteCreatedForms_success() {
    stubFor(
        get(urlEqualTo("/form?type=form&limit=20000"))
            .willReturn(
                okJson(
                    """
                                [
                                    {"_id":"1","name":"AUTO_test"},
                                    {"_id":"2","name":"School"}
                                ]
                            """)));

    stubFor(delete(urlEqualTo("/form/1")).willReturn(ok()));
    stubFor(delete(urlEqualTo("/form/2")).willReturn(ok()));

    api.deleteCreatedForms();

    verify(deleteRequestedFor(urlEqualTo("/form/1")));
    verify(0, deleteRequestedFor(urlEqualTo("/form/2")));
  }

  @Test
  void testCreateFormWithPayload_success() {
    stubFor(
        post(urlEqualTo("/form/"))
            .willReturn(
                created()
                    .withBody(
                        """
                {"id":"NEW_FORM"}
            """)));

    Map result = api.createForm(Map.of("name", "Test"));

    assertThat(result.get("id")).isEqualTo("NEW_FORM");
  }

  @Test
  void testCreateFormFromLines_success() {
    stubFor(
        post(urlEqualTo("/form"))
            .willReturn(
                created()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                {"id":"NEW_FILE_FORM"}
            """)));

    Map result = api.createForm(List.of("line1", "line2"));

    assertThat(result.get("id")).isEqualTo("NEW_FILE_FORM");
  }

  @Test
  void testCreateFormToObject_success() {
    stubFor(
        post(urlEqualTo("/form"))
            .willReturn(
                created()
                    .withBody(
                        """
                {"id":"OBJ123"}
            """)));

    CreatedFormResponse resp = api.createFormToObject(List.of("form"));

    assertThat(resp.getId()).isEqualTo("OBJ123");
  }

  @Test
  void testDeleteFormByName_success() {
    stubFor(delete(urlEqualTo("/MyForm")).willReturn(ok()));

    api.deleteFormByName("MyForm");

    verify(deleteRequestedFor(urlEqualTo("/MyForm")));
  }
}
