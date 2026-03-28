package platform.qa.notifications.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.notifications.pojo.response.InboxNotification;

class NotificationApiTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String BASE = "/api/notifications/inbox";
  private static WireMockServer wireMock;
  private NotificationApi api;

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
    User user = new User("usr", "123");
    user.setToken("token123");
    Service service = new Service("http://localhost:" + wireMock.port(), user);
    api = new NotificationApi(service, service.getUser());
  }

  // ------------------------------------------------------------------------------
  // GET notifications: offset + limit
  // ------------------------------------------------------------------------------
  @Test
  void testGetNotifications_basicSuccess() throws Exception {
    List<InboxNotification> list = List.of(new InboxNotification("1", "now", "test", false, "msg"));

    stubFor(
        get(urlPathEqualTo(BASE))
            .withQueryParam("offset", equalTo("0"))
            .withQueryParam("limit", equalTo("10"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    Response resp = api.getNotifications(0, 10);
    assertThat(resp.getStatusCode()).isEqualTo(SC_OK);
  }

  // ------------------------------------------------------------------------------
  // GET notifications: offset + limit + sort
  // ------------------------------------------------------------------------------
  @Test
  void testGetNotifications_withSort_success() throws Exception {
    List<InboxNotification> list =
        List.of(new InboxNotification("2", "date", "subject", true, "msg"));

    stubFor(
        get(urlPathEqualTo(BASE))
            .withQueryParam("offset", equalTo("1"))
            .withQueryParam("limit", equalTo("5"))
            .withQueryParam("sort", equalTo("desc(createdAt)"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    Response resp = api.getNotifications(1, 5, "desc(createdAt)");
    assertThat(resp.getStatusCode()).isEqualTo(SC_OK);
  }

  // ------------------------------------------------------------------------------
  // GET notifications: requestObject map
  // ------------------------------------------------------------------------------
  @Test
  void testGetNotifications_requestObject_success() throws JsonProcessingException {
    Map<String, Object> requestObj = Map.of("offset", 2, "limit", 3);

    stubFor(
        get(urlPathEqualTo(BASE))
            .withQueryParam("request", equalToJson(mapper.writeValueAsString(requestObj)))
            .willReturn(aResponse().withStatus(SC_OK).withBody("[]")));

    Response resp = api.getNotifications(requestObj);
    assertThat(resp.getStatusCode()).isEqualTo(SC_OK);
  }

  // ------------------------------------------------------------------------------
  // GET notifications: offset + limit + sort + requestObject
  // ------------------------------------------------------------------------------
  @Test
  void testGetNotifications_allParams_success() throws JsonProcessingException {
    Map<String, Object> requestObj = Map.of("key", "value");

    stubFor(
        get(urlPathEqualTo(BASE))
            .withQueryParam("offset", equalTo("0"))
            .withQueryParam("limit", equalTo("10"))
            .withQueryParam("sort", equalTo("asc(subject)"))
            .withQueryParam("request", equalToJson(mapper.writeValueAsString(requestObj)))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[]")));
    Response resp = api.getNotifications(0, 10, "asc(subject)", requestObj);
    assertThat(resp.getStatusCode()).isEqualTo(SC_OK);
  }

  // ------------------------------------------------------------------------------
  // changeState() — 204
  // ------------------------------------------------------------------------------
  @Test
  void testChangeState_success() {
    stubFor(post(urlEqualTo(BASE + "/123/ack")).willReturn(aResponse().withStatus(SC_NO_CONTENT)));

    Response resp = api.changeState("123");
    assertThat(resp.getStatusCode()).isEqualTo(SC_NO_CONTENT);
  }

  // ------------------------------------------------------------------------------
  // acknowledgeNotification() — void + checks 204
  // ------------------------------------------------------------------------------
  @Test
  void testAcknowledgeNotification_success() {
    stubFor(post(urlEqualTo(BASE + "/555/ack")).willReturn(aResponse().withStatus(SC_NO_CONTENT)));

    api.acknowledgeNotification("555");
  }

  // ------------------------------------------------------------------------------
  // getNotificationList(offset, limit)
  // ------------------------------------------------------------------------------
  @Test
  void testGetNotificationList_success() throws Exception {
    List<InboxNotification> list =
        List.of(new InboxNotification("1", "now", "hello", false, "world"));

    stubFor(
        get(urlPathEqualTo(BASE))
            .withQueryParam("offset", equalTo("0"))
            .withQueryParam("limit", equalTo("10"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<InboxNotification> resp = api.getNotificationList(0, 10);
    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getId()).isEqualTo("1");
  }

  // ------------------------------------------------------------------------------
  // getNotificationList(offset,limit,sort)
  // ------------------------------------------------------------------------------
  @Test
  void testGetNotificationList_withSort_success() throws Exception {
    List<InboxNotification> list =
        List.of(new InboxNotification("99", "yesterday", "sorted", true, "msg"));

    stubFor(
        get(urlPathEqualTo(BASE))
            .withQueryParam("offset", equalTo("1"))
            .withQueryParam("limit", equalTo("3"))
            .withQueryParam("sort", equalTo("desc(createdAt)"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<InboxNotification> resp = api.getNotificationList(1, 3, "desc(createdAt)");
    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getSubject()).isEqualTo("sorted");
  }

  // ------------------------------------------------------------------------------
  // getNotificationsWithValidation()
  // ------------------------------------------------------------------------------
  @Test
  void testGetNotificationsWithValidation_success() throws Exception {
    List<InboxNotification> list =
        List.of(new InboxNotification("777", "date", "subj", false, "msg"));

    stubFor(
        get(urlPathEqualTo(BASE))
            .withQueryParam("offset", equalTo("5"))
            .withQueryParam("limit", equalTo("5"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<InboxNotification> resp = api.getNotificationsWithValidation(5, 5);
    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getId()).isEqualTo("777");
  }
}
