package platform.qa.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.ValidatableResponse;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class FormSubmissionValidationApiTest {

  private WireMockServer wireMock;
  private FormSubmissionValidationApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("u", "p");
    user.setToken("token123");

    Service service = new Service("http://localhost:" + wireMock.port() + "/", user);

    api = new FormSubmissionValidationApi(service);
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  // --------------------------------------------------------------------
  // validateForm (normal)
  // --------------------------------------------------------------------
  @Test
  void testValidateForm_success() {
    stubFor(
        post(urlEqualTo("/api/form-submissions/F123/validate"))
            .willReturn(okJson("{\"ok\":true}")));

    ValidatableResponse resp = api.validateForm("F123", "token123", Map.of("field", "value"));

    resp.statusCode(200);
    assertThat(resp.extract().jsonPath().getBoolean("ok")).isTrue();

    verify(
        postRequestedFor(urlEqualTo("/api/form-submissions/F123/validate"))
            .withHeader("X-Access-Token", equalTo("token123")));
  }

  // --------------------------------------------------------------------
  // validateFormMissingToken
  // --------------------------------------------------------------------
  @Test
  void testValidateFormMissingToken_401() {
    stubFor(post(urlEqualTo("/api/form-submissions/F999/validate")).willReturn(unauthorized()));

    ValidatableResponse resp = api.validateFormMissingToken("F999", Map.of("x", 1));

    resp.statusCode(401);
  }

  // --------------------------------------------------------------------
  // validateFormRaw
  // --------------------------------------------------------------------
  @Test
  void testValidateFormRaw_success() {
    stubFor(post(urlEqualTo("/api/form-submissions/A/validate")).willReturn(ok()));

    ValidatableResponse resp = api.validateFormRaw("A", "token123", "{\"raw\":1}");

    resp.statusCode(200);

    verify(
        postRequestedFor(urlEqualTo("/api/form-submissions/A/validate"))
            .withHeader("X-Access-Token", equalTo("token123")));
  }

  // --------------------------------------------------------------------
  // validateField
  // --------------------------------------------------------------------
  @Test
  void testValidateField_success() {
    stubFor(
        post(urlEqualTo("/api/form-submissions/F1/fields/name/validate"))
            .willReturn(okJson("{\"valid\":true}")));

    ValidatableResponse resp = api.validateField("F1", "name", "token123", Map.of("v", "x"));

    resp.statusCode(200);
    assertThat(resp.extract().jsonPath().getBoolean("valid")).isTrue();

    verify(
        postRequestedFor(urlEqualTo("/api/form-submissions/F1/fields/name/validate"))
            .withHeader("X-Access-Token", equalTo("token123")));
  }

  // --------------------------------------------------------------------
  // validateFieldRaw
  // --------------------------------------------------------------------
  @Test
  void testValidateFieldRaw_badRequest() {
    stubFor(
        post(urlEqualTo("/api/form-submissions/F2/fields/code/validate")).willReturn(badRequest()));

    ValidatableResponse resp = api.validateFieldRaw("F2", "code", "token123", "INVALID");

    resp.statusCode(400);
  }

  // --------------------------------------------------------------------
  // checkFields
  // --------------------------------------------------------------------
  @Test
  void testCheckFields_success() {
    stubFor(
        post(urlEqualTo("/api/form-submissions/F77/fields/check"))
            .willReturn(okJson("{\"checked\":true}")));

    ValidatableResponse resp = api.checkFields("F77", "token123", Map.of("a", "b"));

    resp.statusCode(200);
    assertThat(resp.extract().jsonPath().getBoolean("checked")).isTrue();

    verify(
        postRequestedFor(urlEqualTo("/api/form-submissions/F77/fields/check"))
            .withHeader("X-Access-Token", equalTo("token123")));
  }

  // --------------------------------------------------------------------
  // checkFieldsRaw
  // --------------------------------------------------------------------
  @Test
  void testCheckFieldsRaw_403() {
    stubFor(post(urlEqualTo("/api/form-submissions/F100/fields/check")).willReturn(forbidden()));

    ValidatableResponse resp = api.checkFieldsRaw("F100", "token123", "{\"raw\":1}");

    resp.statusCode(403);
  }
}
