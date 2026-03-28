package platform.qa.usermanagement.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.usermanagement.pojo.response.CountResponse;
import platform.qa.usermanagement.pojo.response.ErrorResponse;
import platform.qa.usermanagement.pojo.response.ProcessDefinitionResponse;
import platform.qa.usermanagement.pojo.response.StartProcessInstanceResponse;

@WireMockTest(httpPort = 0)
public class UserProcessManagementApiTest {

  private UserProcessManagementApi api;

  @BeforeEach
  void setUp(WireMockRuntimeInfo wm) {
    User user = new User("test", "pwd");
    user.setToken("TOKEN");

    Service service = new Service(wm.getHttpBaseUrl() + "/", user);
    api = new UserProcessManagementApi(service);
  }

  @Test
  void startProcess_success() {
    stubFor(
        post(urlEqualTo("/user-process-management/api/process-definition/PROC/start"))
            .willReturn(
                okJson(
                    """
            {
              "id": "PI1",
              "processDefinitionId": "DEF1",
              "businessKey": "BK",
              "ended": false
            }
            """)));

    StartProcessInstanceResponse response = api.startProcess("PROC");

    assertThat(response).isNotNull();
  }

  @Test
  void startProcess_error() {
    stubFor(
        get(urlEqualTo("/user-process-management/api/process-definition/PROC"))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
              {
                "message":"Not found"
              }
              """)));

    ErrorResponse error = api.startProcess("PROC", 404);

    assertThat(error).isNotNull();
  }

  @Test
  void getProcessDefinitions_withParams_success() {
    stubFor(
        get(urlPathEqualTo("/user-process-management/api/process-definition"))
            .withQueryParam("active", equalTo("true"))
            .withQueryParam("suspended", equalTo("false"))
            .willReturn(
                okJson(
                    """
          [
            {"id":"DEF1","key":"proc1"}
          ]
          """)));

    List<ProcessDefinitionResponse> defs = api.getProcessDefinitions(true, false);

    assertThat(defs).hasSize(1);
  }

  @Test
  void getCountProcessDefinitions_success() {
    stubFor(
        get(urlEqualTo("/user-process-management/api/process-definition/count"))
            .willReturn(
                okJson(
                    """
          {"count":5}
          """)));

    CountResponse response = api.getCountProcessDefinitions();

    assertThat(response.getCount()).isEqualTo(5);
  }

  @Test
  void getCountProcessDefinitions_error() {
    stubFor(
        get(urlEqualTo("/user-process-management/api/process-definition/count"))
            .willReturn(
                aResponse()
                    .withStatus(500)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
              {"message":"boom"}
              """)));

    ErrorResponse error = api.getCountProcessDefinitions(500);

    assertThat(error).isNotNull();
  }
}
