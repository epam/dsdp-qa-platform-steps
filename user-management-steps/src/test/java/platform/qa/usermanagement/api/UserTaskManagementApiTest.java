package platform.qa.usermanagement.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.usermanagement.pojo.response.*;

class UserTaskManagementApiTest {

  private WireMockServer wireMock;
  private UserTaskManagementApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("user", "pwd");
    user.setToken("TEST_TOKEN");

    Service service = new Service("http://localhost:" + wireMock.port() + "/", user);

    api = new UserTaskManagementApi(service);

    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  // ============ Get Tasks ============

  @Test
  void testGetTasks_success() {
    stubFor(
        get(urlEqualTo("/user-task-management/api/task"))
            .willReturn(
                okJson(
                    """
                                [
                                  {"id":"T1"},
                                  {"id":"T2"}
                                ]
                                """)));

    List<UserTaskResponse> tasks = api.getTasks();

    assertThat(tasks).hasSize(2);
  }

  @Test
  void testGetTasks_error() {
    stubFor(
        get(urlEqualTo("/user-task-management/api/task"))
            .willReturn(
                aResponse()
                    .withStatus(403)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\":\"forbidden\"}")));

    ErrorResponse error = api.getTasks(403);

    assertThat(error).isNotNull();
  }

  @Test
  void testGetTasksByProcessInstanceId_success() {
    stubFor(
        get(urlPathEqualTo("/user-task-management/api/task"))
            .withQueryParam("processInstanceId", equalTo("PI1"))
            .willReturn(
                okJson(
                    """
                                [
                                  {"id":"T1"}
                                ]
                                """)));

    List<UserTaskResponse> tasks = api.getTasksByProcessInstanceId("PI1");

    assertThat(tasks).hasSize(1);
  }

  // ============ Lightweight Tasks ============

  @Test
  void testGetLightweightTasks_success() {
    stubFor(
        get(urlEqualTo("/user-task-management/api/task/lightweight"))
            .willReturn(
                okJson(
                    """
                                [
                                  {"id":"LT1"}
                                ]
                                """)));

    List<UserTaskLightweightResponse> tasks = api.getLightweightTasks();

    assertThat(tasks).hasSize(1);
  }

  @Test
  void testGetLightweightTasksByRootProcessInstanceId_success() {
    stubFor(
        get(urlPathEqualTo("/user-task-management/api/task/lightweight"))
            .withQueryParam("rootProcessInstanceId", equalTo("ROOT1"))
            .willReturn(
                okJson(
                    """
                                [
                                  {"id":"LT1"}
                                ]
                                """)));

    List<UserTaskLightweightResponse> tasks =
        api.getLightweightTasksByRootProcessInstanceId("ROOT1");

    assertThat(tasks).hasSize(1);
  }

  @Test
  void testGetLightweightTasksByRootProcessInstanceId_error() {
    stubFor(
        get(urlPathEqualTo("/user-task-management/api/task/lightweight"))
            .withQueryParam("rootProcessInstanceId", equalTo("ROOT1"))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\":\"bad request\"}")));

    ErrorResponse error = api.getLightweightTasksByRootProcessInstanceId("ROOT1", 400);

    assertThat(error).isNotNull();
  }

  // ============ Count Tasks ============

  @Test
  void testCountTasks_success() {
    stubFor(
        get(urlEqualTo("/user-task-management/api/task/count"))
            .willReturn(okJson("{\"count\":5}")));

    CountResponse count = api.countTasks();

    assertThat(count.getCount()).isEqualTo(5);
  }

  @Test
  void testCountTasks_error() {
    stubFor(
        get(urlEqualTo("/user-task-management/api/task/count"))
            .willReturn(
                aResponse()
                    .withStatus(500)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\":\"error\"}")));

    ErrorResponse error = api.countTasks(500);

    assertThat(error).isNotNull();
  }

  // ============ Get Task By Id ============

  @Test
  void testGetTaskById_success() {
    stubFor(
        get(urlEqualTo("/user-task-management/api/task/T1")).willReturn(okJson("{\"id\":\"T1\"}")));

    UserTaskWithDataResponse task = api.getTaskById("T1");

    assertThat(task.getId()).isEqualTo("T1");
  }

  @Test
  void testGetTaskById_error() {
    stubFor(
        get(urlEqualTo("/user-task-management/api/task/T1"))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\":\"not found\"}")));

    ErrorResponse error = api.getTaskById("T1", 404);

    assertThat(error).isNotNull();
  }

  // ============ Claim Task ============

  @Test
  void testClaimTask_success() {
    stubFor(post(urlEqualTo("/user-task-management/api/task/T1/claim")).willReturn(noContent()));

    api.claimTaskById("T1");

    verify(postRequestedFor(urlEqualTo("/user-task-management/api/task/T1/claim")));
  }

  @Test
  void testClaimTask_error() {
    stubFor(
        post(urlEqualTo("/user-task-management/api/task/T1/claim"))
            .willReturn(
                aResponse()
                    .withStatus(409)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\":\"conflict\"}")));

    ErrorResponse error = api.claimTaskById("T1", 409);

    assertThat(error).isNotNull();
  }

  // ============ Complete Task ============

  @Test
  void testCompleteTask_success() {
    stubFor(
        post(urlEqualTo("/user-task-management/api/task/T1/complete"))
            .willReturn(
                okJson(
                    """
            {
              "id":"T1",
              "processInstanceId":"PI1",
              "rootProcessInstanceId":"RPI1",
              "rootProcessInstanceEnded":false,
              "variables":{}
            }
            """)));

    CompleteResponse response = api.completeTaskById("T1", Map.of("data", "x"));

    assertThat(response).isNotNull();
  }

  @Test
  void testCompleteTask_error() {
    stubFor(
        post(urlEqualTo("/user-task-management/api/task/T1/complete"))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\":\"bad request\"}")));

    ErrorResponse error = api.completeTaskById("T1", Map.of(), 400);

    assertThat(error).isNotNull();
  }

  // ============ Save Task ============

  @Test
  void testSaveTask_success() {
    stubFor(post(urlEqualTo("/user-task-management/api/task/T1/save")).willReturn(ok()));

    api.saveTaskById("T1", Map.of("data", "x"));

    verify(postRequestedFor(urlEqualTo("/user-task-management/api/task/T1/save")));
  }

  @Test
  void testSaveTask_error() {
    stubFor(
        post(urlEqualTo("/user-task-management/api/task/T1/save"))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\":\"error\"}")));

    ErrorResponse error = api.saveTaskById("T1", Map.of(), 400);

    assertThat(error).isNotNull();
  }
}
