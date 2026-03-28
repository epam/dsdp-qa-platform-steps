package platform.qa.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.Task;
import platform.qa.entities.TaskHistory;
import platform.qa.entities.User;

class TaskApiTest {

  private WireMockServer wireMock;
  private TaskApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("user", "pwd");
    user.setToken("TEST_TOKEN");
    Service service = new Service("http://localhost:" + wireMock.port(), user);

    api = new TaskApi(service);

    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  @Test
  void testGetTasksInstances_success() {
    stubFor(get(urlEqualTo("/api/task/")).willReturn(okJson("[{\"id\":\"T1\"},{\"id\":\"T2\"}]")));

    List<Task> tasks = api.getTasksInstances();

    assertThat(tasks).hasSize(2);
  }

  @Test
  void testGetTaskCountByProcessInstanceId_success() {
    stubFor(
        get(urlPathEqualTo("/api/task/count"))
            .withQueryParam("processInstanceId", equalTo("PI1"))
            .willReturn(okJson("{\"count\":3}")));

    int count = api.getTaskCountByProcessInstanceId("PI1");

    assertThat(count).isEqualTo(3);
  }

  @Test
  void testGetTaskCountByProcessDefinitionId_success() {
    stubFor(
        get(urlPathEqualTo("/api/task/count"))
            .withQueryParam("processDefinitionId", equalTo("PD1"))
            .willReturn(okJson("{\"count\":2}")));

    int count = api.getTaskCountByProcessDefinitionId("PD1");

    assertThat(count).isEqualTo(2);
  }

  @Test
  void testGetTasksByProcessDefinitionId_success() {
    stubFor(
        get(urlPathEqualTo("/api/task"))
            .withQueryParam("processDefinitionId", equalTo("PI2"))
            .willReturn(okJson("[{\"id\":\"T3\"}]")));

    List<Task> tasks = api.getTasksByProcessDefinitionId("PI2");

    assertThat(tasks).hasSize(1);
  }

  @Test
  void testGetTasksByProcessDefinitionName_success() {
    stubFor(
        get(urlPathEqualTo("/api/task/"))
            .withQueryParam("processDefinitionName", equalTo("ProcName"))
            .willReturn(okJson("[{\"id\":\"T5\"}]")));

    List<Task> tasks = api.getTasksByProcessDefinitionName("ProcName");

    assertThat(tasks).hasSize(1);
  }

  @Test
  @SuppressWarnings("deprecation")
  void testGetTasksHistoryByDefinitionId_success() {
    stubFor(
        get(urlPathEqualTo("/api/history/task"))
            .withQueryParam("processDefinitionId", equalTo("PD3"))
            .willReturn(okJson("[{\"id\":\"H1\"}]")));

    List<TaskHistory> history = api.getTasksHistoryByDefinitionId("PD3");

    assertThat(history).hasSize(1);
  }

  @Test
  void testSetTaskAssignee_success() {
    stubFor(post(urlEqualTo("/api/task/TASK1/assignee")).willReturn(noContent()));

    api.setTaskAssignee("TASK1", "user1");

    verify(postRequestedFor(urlEqualTo("/api/task/TASK1/assignee")));
  }

  @Test
  void testClaimTaskById_success() {
    stubFor(post(urlEqualTo("/api/task/TASK2/claim")).willReturn(noContent()));

    api.claimTaskById("TASK2", "user2");

    verify(postRequestedFor(urlEqualTo("/api/task/TASK2/claim")));
  }

  @Test
  void testCompleteTaskById_success() {
    stubFor(post(urlEqualTo("/api/task/TASK3/complete")).willReturn(noContent()));

    api.completeTaskById("TASK3");

    verify(postRequestedFor(urlEqualTo("/api/task/TASK3/complete")));
  }

  @Test
  @SuppressWarnings("deprecation")
  void testGetTasksHistoryByProcessInstanceId_success() {
    stubFor(
        get(urlPathEqualTo("/api/history/task"))
            .withQueryParam("processInstanceId", equalTo("PI4"))
            .willReturn(okJson("[{\"id\":\"H2\"}]")));

    List<TaskHistory> history = api.getTasksHistoryByProcessInstanceId("PI4");

    assertThat(history).hasSize(1);
  }

  @Test
  @SuppressWarnings("deprecation")
  void testGetTasksHistory_success() {
    stubFor(get(urlEqualTo("/api/history/task")).willReturn(okJson("[{\"id\":\"H3\"}]")));

    List<TaskHistory> history = api.getTasksHistory();

    assertThat(history).hasSize(1);
  }

  @Test
  void testGetTaskById_success() {
    stubFor(get(urlEqualTo("/api/task/TASK4")).willReturn(okJson("{\"id\":\"TASK4\"}")));

    Task task = api.getTaskById("TASK4");

    assertThat(task.getId()).isEqualTo("TASK4");
  }

  @Test
  void testGetTasksByName_success() {
    stubFor(
        get(urlPathEqualTo("/api/task/"))
            .withQueryParam("name", equalTo("MyTask"))
            .willReturn(okJson("[{\"id\":\"T6\"}]")));

    List<Task> tasks = api.getTasksByName("MyTask");

    assertThat(tasks).hasSize(1);
  }

  @Test
  void testGetTasksByAssignee_success() {
    stubFor(
        get(urlPathEqualTo("/api/task/"))
            .withQueryParam("assignee", equalTo("assignee1"))
            .willReturn(okJson("[{\"id\":\"T7\"}]")));

    List<Task> tasks = api.getTasksByAssignee("assignee1");

    assertThat(tasks).hasSize(1);
  }
}
