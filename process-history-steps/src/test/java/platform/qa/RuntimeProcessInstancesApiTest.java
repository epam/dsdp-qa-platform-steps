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
import platform.qa.entities.CountResponse;
import platform.qa.entities.ErrorResponse;
import platform.qa.entities.RuntimeProcessInstance;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class RuntimeProcessInstancesApiTest {

  @RegisterExtension
  static WireMockExtension wm =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  RuntimeProcessInstancesApi api;

  @BeforeEach
  void setup() {
    User user = new User("u", "p");
    user.setToken("TOKEN");

    Service service = new Service(wm.baseUrl() + "/", user);
    api = new RuntimeProcessInstancesApi(service);
  }

  @Test
  void getRuntimeProcessInstances_success() {
    wm.stubFor(
        get(urlPathEqualTo("/api/runtime/process-instances"))
            .withQueryParam("limit", equalTo("100"))
            .withQueryParam("sort", equalTo("desc(startTime)"))
            .willReturn(
                okJson(
                    """
                                        [
                                          { "id": "PI1" },
                                          { "id": "PI2" }
                                        ]
                                        """)));

    List<RuntimeProcessInstance> result = api.getRuntimeProcessInstances();

    assertThat(result).hasSize(2);
  }

  @Test
  void getRuntimeProcessInstancesCount_success() {
    wm.stubFor(
        get(urlEqualTo("/api/runtime/process-instances/count"))
            .willReturn(okJson("{ \"count\": 5 }")));

    CountResponse response = api.getRuntimeProcessInstancesCount();

    assertThat(response.getCount()).isEqualTo(5);
  }

  @Test
  void getRuntimeProcessInstancesCount_error() {
    wm.stubFor(
        get(urlEqualTo("/api/runtime/process-instances/count"))
            .willReturn(
                aResponse()
                    .withStatus(500)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{ \"message\": \"boom\" }")));

    ErrorResponse error = api.getRuntimeProcessInstancesCount(500);

    assertThat(error.getMessage()).contains("boom");
  }

  @Test
  void getRuntimeProcessInstances_invalidParams() {
    wm.stubFor(
        get(urlPathEqualTo("/api/runtime/process-instances"))
            .withQueryParam("foo", equalTo("bar"))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{ \"message\": \"invalid param\" }")));

    ErrorResponse error = api.getRuntimeProcessInstancesInvalidParam(Map.of("foo", "bar"), 400);

    assertThat(error.getMessage()).contains("invalid");
  }

  @Test
  void getRuntimeProcessInstances_error() {
    wm.stubFor(
        get(urlPathEqualTo("/api/runtime/process-instances"))
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{ \"message\": \"unauthorized\" }")));

    ErrorResponse error = api.getRuntimeProcessInstances(401);

    assertThat(error.getMessage()).contains("unauthorized");
  }
}
