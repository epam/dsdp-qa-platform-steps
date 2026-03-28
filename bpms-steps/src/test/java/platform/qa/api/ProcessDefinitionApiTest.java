package platform.qa.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Definition;
import platform.qa.entities.Instance;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class ProcessDefinitionApiTest {

  private WireMockServer wireMock;
  private ProcessDefinitionApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("user", "pwd");
    user.setToken("TEST_TOKEN");
    Service service = new Service("http://localhost:" + wireMock.port(), user);

    api = new ProcessDefinitionApi(service);

    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  @Test
  void testGetAllDefinitionKeys_success() {
    stubFor(
        get(urlEqualTo("/api/process-definition"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"key":"DEF_1"},
                                          {"key":"DEF_2"}
                                        ]
                                        """)));

    List<String> keys = api.getAllDefinitionKeys();

    assertThat(keys).containsExactly("DEF_1", "DEF_2");
  }

  @Test
  void testGetAllDefinitions_success() {
    stubFor(
        get(urlEqualTo("/api/process-definition"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"1","key":"DEF_1","name":"Test 1"},
                                          {"id":"2","key":"DEF_2","name":"Test 2"}
                                        ]
                                        """)));

    List<Definition> defs = api.getAllDefinitions();

    assertThat(defs).hasSize(2);
    assertThat(defs.get(0).getKey()).isEqualTo("DEF_1");
  }

  @Test
  void testGetDefinitionNameByKey_success() {
    stubFor(
        get(urlEqualTo("/api/process-definition/key/PROC"))
            .willReturn(
                okJson(
                    """
                                        {"id":"1","name":"MyProcess"}
                                        """)));

    String name = api.getDefinitionNameByKey("PROC");

    assertThat(name).isEqualTo("MyProcess");
  }

  @Test
  void testGetDefinitionIdByName_success() {
    stubFor(
        get(urlEqualTo("/api/process-definition?name=TestProc"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"DEF_ID"}
                                        ]
                                        """)));

    String id = api.getDefinitionIdByName("TestProc");

    assertThat(id).isEqualTo("DEF_ID");
  }

  @Test
  void testGetDefinitionById_success() {
    stubFor(
        get(urlEqualTo("/api/process-definition/DEF123"))
            .willReturn(
                okJson(
                    """
                                        {"id":"DEF123","key":"KEY","name":"Proc"}
                                        """)));

    Map result = api.getDefinitionById("DEF123");
    assertThat(result).containsEntry("id", "DEF123");
  }

  @Test
  void testGetDefinitionByKey_success() {
    stubFor(
        get(urlEqualTo("/api/process-definition?processDefinitionKey=KEY"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"1","key":"KEY"}
                                        ]
                                        """)));

    List result = api.getDefinitionByKey("KEY");

    assertThat(result).hasSize(1);
  }

  @Test
  void testGetDefinitionByName_success() {
    stubFor(
        get(urlEqualTo("/api/process-definition?name=Proc"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"1","name":"Proc"}
                                        ]
                                        """)));

    Map result = api.getDefinitionByName("Proc");
    assertThat(result).containsEntry("name", "Proc");
  }

  @Test
  void testGetProcessDefinitionByName_success() {
    stubFor(
        get(urlEqualTo("/api/process-definition?name=Proc"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"1","name":"Proc","key":"KEY"}
                                        ]
                                        """)));

    Definition def = api.getProcessDefinitionByName("Proc");

    assertThat(def.getKey()).isEqualTo("KEY");
  }

  @Test
  void testDeleteDefinitionByKey_success() {
    stubFor(delete(urlEqualTo("/api/process-definition/key/KEY/?cascade=true")).willReturn(ok()));

    boolean result = api.deleteDefinitionByKey("KEY");

    assertThat(result).isTrue();
  }

  @Test
  void testStartProcessInstance_success() {
    stubFor(
        post(urlEqualTo("/api/process-definition/key/KEY/start"))
            .willReturn(
                okJson(
                    """
                                        {"id":"INST123"}
                                        """)));

    String id = api.startProcessInstance("KEY");

    assertThat(id).isEqualTo("INST123");
  }

  @Test
  void testStartProcessInstanceWithInitiator_success() {
    stubFor(
        post(urlEqualTo("/api/process-definition/key/KEY/start"))
            .willReturn(
                okJson(
                    """
                                        {"id":"I1","definitionId":"D1"}
                                        """)));

    Instance instance = api.startProcessInstanceWithInitiator("KEY", "USER");

    assertThat(instance.getId()).isEqualTo("I1");
  }

  @Test
  void testGetProcessDefinitionById_success() {
    stubFor(
        get(urlEqualTo("/api/process-definition/DEF"))
            .willReturn(
                okJson(
                    """
                                        {"id":"DEF","key":"KEY"}
                                        """)));

    Definition def = api.getProcessDefinitionById("DEF");

    assertThat(def.getKey()).isEqualTo("KEY");
  }

  @Test
  void testStartProcessInstanceByDefinitionId_success() {
    stubFor(
        post(urlEqualTo("/api/process-definition/DEF/start"))
            .willReturn(
                okJson(
                    """
                                        {"id":"INST"}
                                        """)));

    Instance instance = api.startProcessInstanceByDefinitionId("DEF");

    assertThat(instance.getId()).isEqualTo("INST");
  }

  @Test
  void testSuspendProcessDefinitionById_success() {
    stubFor(put(urlEqualTo("/api/process-definition/DEF/suspended/")).willReturn(noContent()));

    int status = api.suspendProcessDefinitionById(true, "DEF");

    assertThat(status).isEqualTo(204);
  }

  @Test
  void testSuspendProcessDefinitionByKey_success() {
    stubFor(put(urlEqualTo("/api/process-definition/key/KEY/suspended/")).willReturn(noContent()));

    int status = api.suspendProcessDefinitionByKey(true, "KEY");

    assertThat(status).isEqualTo(204);
  }

  @Test
  void testDeleteCreatedProcessDefinitions_success() {
    stubFor(
        get(urlEqualTo("/api/process-definition/?keyLike=%25_AUTO"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"key":"TEST_AUTO"}
                                        ]
                                        """)));

    stubFor(
        delete(urlEqualTo("/api/process-definition/key/TEST_AUTO/?cascade=true")).willReturn(ok()));

    api.deleteCreatedProcessDefinitions();

    verify(deleteRequestedFor(urlEqualTo("/api/process-definition/key/TEST_AUTO/?cascade=true")));
  }
}
