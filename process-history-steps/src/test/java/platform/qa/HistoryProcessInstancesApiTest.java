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
import platform.qa.entities.HistoryProcessInstance;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class HistoryProcessInstancesApiTest {

  @RegisterExtension
  static WireMockExtension wm =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  private HistoryProcessInstancesApi api;

  @BeforeEach
  void setup() {
    User user = new User("u", "p");
    user.setToken("TOKEN");

    Service service = new Service(wm.baseUrl() + "/", user);
    api = new HistoryProcessInstancesApi(service);
  }

  @Test
  void getHistoryProcessInstances_success() {
    wm.stubFor(
        get(urlPathEqualTo("/api/history/process-instances"))
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
                                                "processInstanceId": "PI1",
                                                "processDefinitionId": "DEF1",
                                                "processDefinitionKey": "proc-key",
                                                "processDefinitionName": "Test Process",
                                                "businessKey": "BK-1",
                                                "startTime": "2024-01-01T10:00:00Z",
                                                "endTime": "2024-01-01T10:05:00Z",
                                                "startUserId": "user1"
                                              }
                                            ]
                                            """)));

    List<HistoryProcessInstance> result = api.getHistoryProcessInstances();

    assertThat(result).hasSize(1);
    HistoryProcessInstance pi = result.get(0);

    assertThat(pi.getProcessInstanceId()).isEqualTo("PI1");
    assertThat(pi.getProcessDefinitionId()).isEqualTo("DEF1");
    assertThat(pi.getProcessDefinitionKey()).isEqualTo("proc-key");
  }

  @Test
  void getHistoryProcessInstances_invalidParams() {
    wm.stubFor(
        get(urlPathEqualTo("/api/history/process-instances"))
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

    ErrorResponse error = api.getHistoryProcessInstancesInvalidParam(Map.of("limit", "abc"), 400);

    assertThat(error).isNotNull();
    assertThat(error.getCode()).isEqualTo("BAD_REQUEST");
  }

  @Test
  void getHistoryProcessInstances_error() {
    wm.stubFor(
        get(urlPathEqualTo("/api/history/process-instances"))
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

    ErrorResponse error = api.getHistoryProcessInstances(401);

    assertThat(error).isNotNull();
    assertThat(error.getCode()).isEqualTo("UNAUTHORIZED");
  }
}
