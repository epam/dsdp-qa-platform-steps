package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.response.MasterVersionInfoResponse;

public class MasterVersionApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private MasterVersionApiSteps steps;

  @BeforeAll
  static void beforeAll() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());
  }

  @AfterAll
  static void afterAll() {
    wireMock.stop();
  }

  @BeforeEach
  void setup() {
    Service service = new Service("http://localhost:" + wireMock.port(), new User("u", "t"));
    steps = new MasterVersionApiSteps(service);
  }

  // =========================================================================
  // GET MASTER VERSION INFO — SUCCESS
  // =========================================================================
  @Test
  void testGetMasterVersionInfo_success() throws Exception {
    MasterVersionInfoResponse resp =
        MasterVersionInfoResponse.builder()
            .id("master")
            .name("Master Version")
            .description("Main registry version")
            .published(true)
            .build();

    stubFor(
        get(urlEqualTo("/versions/master/"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(resp))));

    MasterVersionInfoResponse result = steps.getMasterVersionInfo();

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("master");
    assertThat(result.getName()).isEqualTo("Master Version");

    wireMock.verify(getRequestedFor(urlEqualTo("/versions/master/")));
  }
}
