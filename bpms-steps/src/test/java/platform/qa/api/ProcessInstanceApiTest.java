package platform.qa.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Instance;
import platform.qa.entities.InstanceHistory;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class ProcessInstanceApiTest {

  private WireMockServer wireMock;
  private ProcessInstanceApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("user", "pwd");
    user.setToken("TEST_TOKEN");
    Service service = new Service("http://localhost:" + wireMock.port(), user);

    api = new ProcessInstanceApi(service);

    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  @Test
  void testGetProcessInstanceVariables_success() {
    stubFor(
        get(urlEqualTo("/api/process-instance/PROC1/variables"))
            .willReturn(
                okJson(
                    """
                                        {
                                          "var1": {"value":"123"},
                                          "var2": {"value":"abc"}
                                        }
                                        """)));

    Map<String, Object> vars = api.getProcessInstanceVariables("PROC1");

    assertThat(vars).containsKey("var1");
  }

  @Test
  void testSuspendProcessInstance_success() {
    stubFor(put(urlEqualTo("/api/process-instance/INST1/suspended")).willReturn(noContent()));

    int status = api.suspendProcessInstance(true, "INST1");

    assertThat(status).isEqualTo(204);
  }

  @Test
  void testDeleteProcessInstance_success() {
    stubFor(delete(urlEqualTo("/api/process-instance/INST1")).willReturn(noContent()));

    int status = api.deleteProcessInstance("INST1");

    assertThat(status).isEqualTo(204);
  }

  @Test
  void testDeleteProcessInstanceWithoutStatusCodeCheck_success() {
    stubFor(
        delete(urlEqualTo("/api/process-instance/INST2")).willReturn(aResponse().withStatus(500)));

    api.deleteProcessInstanceWithoutStatusCodeCheck("INST2");

    verify(deleteRequestedFor(urlEqualTo("/api/process-instance/INST2")));
  }

  @Test
  void testGetProcessInstanceStatus_success() {
    stubFor(
        get(urlEqualTo("/api/process-instance/INST3"))
            .willReturn(
                okJson(
                    """
                                        {"suspended":false}
                                        """)));

    String status = api.getProcessInstanceStatus("INST3");

    assertThat(status).isEqualTo("false");
  }

  @Test
  void testGetProcessInstanceStatusCode_default_success() {
    stubFor(
        get(urlEqualTo("/api/process-instance/INST4"))
            .willReturn(
                okJson(
                    """
                                        {"id":"INST4"}
                                        """)));

    int status = api.getProcessInstanceStatusCode("INST4");

    assertThat(status).isEqualTo(200);
  }

  @Test
  void testGetProcessInstanceStatusCode_withExpectedCode_success() {
    stubFor(get(urlEqualTo("/api/process-instance/INST5")).willReturn(aResponse().withStatus(404)));

    int status = api.getProcessInstanceStatusCode("INST5", 404);

    assertThat(status).isEqualTo(404);
  }

  @Test
  void testGetProcessInstancesList_success() {
    stubFor(
        get(urlEqualTo("/api/process-instance/"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"1"},
                                          {"id":"2"}
                                        ]
                                        """)));

    List<Instance> list = api.getProcessInstancesList();

    assertThat(list).hasSize(2);
  }

  @Test
  void testGetProcessInstanceCount_success() {
    stubFor(
        get(urlEqualTo("/api/history/process-instance/count/"))
            .willReturn(
                okJson(
                    """
                                        {"count":5}
                                        """)));

    int count = api.getProcessInstanceCount();

    assertThat(count).isEqualTo(5);
  }

  @Test
  void testGetProcessInstanceById_success() {
    stubFor(
        get(urlEqualTo("/api/process-instance/INST6"))
            .willReturn(
                okJson(
                    """
                                        {"id":"INST6","definitionId":"DEF"}
                                        """)));

    Instance instance = api.getProcessInstanceById("INST6");

    assertThat(instance.getId()).isEqualTo("INST6");
  }

  @Test
  void testDeleteProcessInstanceViaPost_success() {
    stubFor(post(urlEqualTo("/api/process-instance/delete")).willReturn(okJson("{\"deleted\":1}")));

    String response = api.deleteProcessInstanceViaPost("INST7");

    assertThat(response).isNotNull();
  }

  @Test
  @SuppressWarnings("deprecation")
  void testGetProcessInstanceHistoryById_success() {
    stubFor(
        get(urlEqualTo("/api/history/process-instance/HIST1"))
            .willReturn(
                okJson(
                    """
                                        {"id":"HIST1","state":"COMPLETED"}
                                        """)));

    InstanceHistory history = api.getProcessInstanceHistoryById("HIST1");

    assertThat(history.getState()).isEqualTo("COMPLETED");
  }

  @Test
  void testGetProcessInstanceStateById_success() {
    stubFor(
        get(urlEqualTo("/api/history/process-instance/HIST2"))
            .willReturn(
                okJson(
                    """
                                        {"id":"HIST2","state":"ACTIVE"}
                                        """)));

    String state = api.getProcessInstanceStateById("HIST2");

    assertThat(state).isEqualTo("ACTIVE");
  }
}
