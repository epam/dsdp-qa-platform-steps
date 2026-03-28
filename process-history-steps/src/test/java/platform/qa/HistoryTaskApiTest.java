package platform.qa;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import platform.qa.entities.ErrorResponse;
import platform.qa.entities.HistoryTask;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class HistoryTaskApiTest {

  @RegisterExtension
  static WireMockExtension wm =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  private HistoryTaskApi api;

  @BeforeEach
  void setup() {
    User user = new User("u", "p");
    user.setToken("TOKEN");

    Service service = new Service(wm.baseUrl() + "/", user);
    api = new HistoryTaskApi(service);
  }

  @Test
  void getHistoryTasks_success() {
    wm.stubFor(
        get(urlPathEqualTo("/api/history/tasks"))
            .withQueryParam("limit", equalTo("100"))
            .withQueryParam("sort", equalTo("desc(endTime)"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                            [
                                              {
                                                "activityInstanceId": "ACT1",
                                                "taskDefinitionKey": "approveTask",
                                                "taskDefinitionName": "Approve request",
                                                "processInstanceId": "PI1",
                                                "processDefinitionId": "DEF1",
                                                "processDefinitionKey": "proc-key",
                                                "processDefinitionName": "Test Process",
                                                "rootProcessInstanceId": "ROOT1",
                                                "businessKey": "BK-1",
                                                "startTime": "2024-01-01T10:00:00Z",
                                                "endTime": "2024-01-01T10:05:00Z",
                                                "assignee": "user1"
                                              }
                                            ]
                                            """)));

    List<HistoryTask> result = api.getHistoryTasks();

    assertThat(result).hasSize(1);

    HistoryTask task = result.get(0);
    assertThat(task.getActivityInstanceId()).isEqualTo("ACT1");
    assertThat(task.getProcessInstanceId()).isEqualTo("PI1");
    assertThat(task.getTaskDefinitionKey()).isEqualTo("approveTask");
  }

  @Test
  void getHistoryTasks_invalidParams() {
    wm.stubFor(
        get(urlPathEqualTo("/api/history/tasks"))
            .withQueryParam("limit", equalTo("abc"))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                {
                                                  "code": "BAD_REQUEST",
                                                  "message": "Invalid limit"
                                                }
                                                """)));

    ErrorResponse error = api.getHistoryTasksInvalidParam(Map.of("limit", "abc"), 400);

    assertThat(error.getCode()).isEqualTo("BAD_REQUEST");
  }

  @Test
  void getHistoryTasks_error() {
    wm.stubFor(
        get(urlPathEqualTo("/api/history/tasks"))
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                {
                                                  "code": "UNAUTHORIZED"
                                                }
                                                """)));

    ErrorResponse error = api.getHistoryTasks(401);

    assertThat(error.getCode()).isEqualTo("UNAUTHORIZED");
  }
}
