package platform.qa.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class DecisionDefinitionApiTest {

  private WireMockServer wireMock;
  private DecisionDefinitionApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("user", "pwd");
    user.setToken("TEST_TOKEN");
    Service service = new Service("http://localhost:" + wireMock.port(), user);

    api = new DecisionDefinitionApi(service);

    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  @Test
  void testGetDecisionDefinitionByKey_success() {
    stubFor(
        get(urlEqualTo("/api/decision-definition/key/DEC_KEY"))
            .willReturn(
                okJson(
                    """
                                        {
                                          "id":"DEC1",
                                          "key":"DEC_KEY",
                                          "name":"Decision Name"
                                        }
                                        """)));

    Map<Object, Object> result = api.getDecisionDefinitionByKey("DEC_KEY");

    assertThat(result).containsEntry("id", "DEC1").containsEntry("key", "DEC_KEY");
  }

  @Test
  void testGetDecisionDefinitionById_success() {
    stubFor(
        get(urlEqualTo("/api/decision-definition/id/DEC_ID"))
            .willReturn(
                okJson(
                    """
                                        {
                                          "id":"DEC_ID",
                                          "key":"DEC_KEY",
                                          "name":"Decision Name"
                                        }
                                        """)));

    Map<Object, Object> result = api.getDecisionDefinitionById("DEC_ID");

    assertThat(result).containsEntry("id", "DEC_ID").containsEntry("key", "DEC_KEY");
  }

  @Test
  void testGetAllDecisionDefinitions_success() {
    stubFor(
        get(urlEqualTo("/api/decision-definition"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"D1","key":"DEC1"},
                                          {"id":"D2","key":"DEC2"}
                                        ]
                                        """)));

    List<HashMap> decisions = api.getAllDecisionDefinitions();

    assertThat(decisions).hasSize(2);
    assertThat(decisions.get(0)).containsEntry("id", "D1");
  }
}
