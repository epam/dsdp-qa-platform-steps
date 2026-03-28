package platform.qa.notifications.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class NotificationTemplateApiTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String BASE = "/api/notifications/templates";
  private static WireMockServer wireMock;
  private NotificationTemplateApi api;

  @BeforeAll
  static void beforeAll() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());
  }

  @AfterAll
  static void afterAll() {
    wireMock.stop();
  }

  @BeforeEach
  void init() {
    User user = new User("usr", "pwd");
    user.setToken("token123");

    Service service = new Service("http://localhost:" + wireMock.port(), user);
    api = new NotificationTemplateApi(service, service.getUser());
  }

  // ----------------------------------------------------------
  // PUT template (basic response)
  // ----------------------------------------------------------
  @Test
  void testPutNotificationTemplate_success() throws Exception {
    Map<String, Object> payload = Map.of("subject", "hello");

    stubFor(
        put(urlMatching("/api/notifications/templates/email.*welcome"))
            .withRequestBody(equalToJson(mapper.writeValueAsString(payload)))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(payload))));

    Response resp = api.putNotificationTemplate("email", "welcome", payload);

    assertThat(resp.getStatusCode()).isEqualTo(200);
  }

  // ----------------------------------------------------------
  // PUT template with validation
  // ----------------------------------------------------------
  @Test
  void testCreateOrUpdateNotificationTemplate_success() throws Exception {
    Map<String, Object> payload = Map.of("body", "Test template");

    stubFor(
        put(urlMatching("/api/notifications/templates/sms.*otp"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(payload))));

    api.createOrUpdateNotificationTemplate("sms", "otp", payload);
  }

  // ----------------------------------------------------------
  // GET all templates
  // ----------------------------------------------------------
  @Test
  void testGetAllNotificationTemplates_success() throws Exception {
    List<Map<String, Object>> templates =
        List.of(Map.of("id", "1", "name", "welcome"), Map.of("id", "2", "name", "otp"));

    stubFor(
        get(urlEqualTo(BASE))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(templates))));

    Response resp = api.getAllNotificationTemplates();

    assertThat(resp.getStatusCode()).isEqualTo(SC_OK);
  }

  // ----------------------------------------------------------
  // GET all templates as List<Map<String,Object>>
  // ----------------------------------------------------------
  @Test
  void testGetNotificationTemplatesList_success() throws Exception {
    List<Map<String, Object>> templates =
        List.of(Map.of("id", "123", "channel", "email"), Map.of("id", "456", "channel", "sms"));

    stubFor(
        get(urlEqualTo(BASE))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(templates))));

    List<Map<String, Object>> resp = api.getNotificationTemplatesList();

    assertThat(resp).hasSize(2);
    assertThat(resp.get(0).get("id")).isEqualTo("123");
  }

  // ----------------------------------------------------------
  // GET all templates with validation
  // ----------------------------------------------------------
  @Test
  void testGetAllNotificationTemplatesWithValidation_success() throws Exception {
    List<Map<String, Object>> templates = List.of(Map.of("id", "abc", "channel", "push"));

    stubFor(
        get(urlEqualTo(BASE))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(templates))));

    List<Map<String, Object>> resp = api.getAllNotificationTemplatesWithValidation();

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).get("id")).isEqualTo("abc");
  }

  // ----------------------------------------------------------
  // DELETE template — basic
  // ----------------------------------------------------------
  @Test
  void testDeleteNotificationTemplate_success() {
    stubFor(
        delete(urlEqualTo("/api/notifications/templates/111"))
            .willReturn(aResponse().withStatus(SC_NO_CONTENT)));

    Response resp = api.deleteNotificationTemplate("111");

    assertThat(resp.getStatusCode()).isEqualTo(SC_NO_CONTENT);
  }

  // ----------------------------------------------------------
  // DELETE template with validation
  // ----------------------------------------------------------
  @Test
  void testDeleteNotificationTemplateWithValidation_success() {
    stubFor(
        delete(urlEqualTo("/api/notifications/templates/777"))
            .willReturn(aResponse().withStatus(SC_NO_CONTENT)));

    api.deleteNotificationTemplateWithValidation("777");
  }
}
