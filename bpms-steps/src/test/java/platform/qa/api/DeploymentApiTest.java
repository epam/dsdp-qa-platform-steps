package platform.qa.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.*;
import platform.qa.entities.Deployment;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class DeploymentApiTest {

  private WireMockServer wireMock;
  private DeploymentApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("user", "pwd");
    user.setToken("TEST_TOKEN");
    Service service = new Service("http://localhost:" + wireMock.port(), user);

    api = new DeploymentApi(service);

    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  @Test
  void testGetAllDeployments_success() {
    stubFor(
        get(urlEqualTo("/api/deployment"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"D1","name":"Dep1"},
                                          {"id":"D2","name":"Dep2"}
                                        ]
                                        """)));

    List<HashMap> deployments = api.getAllDeployments();

    assertThat(deployments).hasSize(2);
    assertThat(deployments.get(0)).containsEntry("id", "D1");
  }

  @Test
  void testDeleteDeploymentById_success() {
    stubFor(
        delete(urlPathEqualTo("/api/deployment/DEP1"))
            .withQueryParam("cascade", equalTo("true"))
            .willReturn(noContent()));

    api.deleteDeploymentById("DEP1");

    verify(
        deleteRequestedFor(urlPathEqualTo("/api/deployment/DEP1"))
            .withQueryParam("cascade", equalTo("true")));
  }

  @Test
  void testCreateDeployment_success() throws Exception {
    stubFor(
        post(urlEqualTo("/api/deployment/create"))
            .willReturn(
                okJson(
                    """
                                        {
                                          "id":"DEPLOY123",
                                          "name":"TestDeployment"
                                        }
                                        """)));

    File tmpFile = Files.createTempFile("deploy", ".bpmn").toFile();
    tmpFile.deleteOnExit();

    Deployment deployment = api.createDeployment(tmpFile, "TestDeployment");

    assertThat(deployment.getId()).isEqualTo("DEPLOY123");

    verify(postRequestedFor(urlEqualTo("/api/deployment/create")));
  }
}
