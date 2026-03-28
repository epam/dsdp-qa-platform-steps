package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.grouping.BpGrouping;
import platform.qa.registry.management.dto.response.BusinessProcessGroups;

class CandidateBpGroupingApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private CandidateBpGroupingApiSteps steps;

  @BeforeAll
  static void startWireMock() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());
  }

  @AfterAll
  static void stopWireMock() {
    wireMock.stop();
  }

  @BeforeEach
  void setup() {
    Service service =
        new Service("http://localhost:" + wireMock.port(), new User("usr", "token123"));
    steps = new CandidateBpGroupingApiSteps(service);
  }

  // ======================================================================
  // CREATE GROUP — SUCCESS
  // ======================================================================
  @Test
  void testCreateBusinessProcessGroup_success() {
    stubFor(
        post(urlEqualTo("/versions/candidates/v1/business-process-groups"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody("\"SUCCESS\"")));

    String result = steps.createBusinessProcessGroup("v1", new BpGrouping());

    assertThat(result).isEqualTo("\"SUCCESS\"");

    wireMock.verify(
        postRequestedFor(urlEqualTo("/versions/candidates/v1/business-process-groups"))
            .withHeader("Content-Type", containing("application/json")));
  }

  // ======================================================================
  // CREATE GROUP — BAD REQUEST
  // ======================================================================
  @Test
  void testCreateBusinessProcessGroup_badRequest() {
    stubFor(
        post(urlEqualTo("/versions/candidates/v1/business-process-groups"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));

    assertThrows(
        AssertionError.class, () -> steps.createBusinessProcessGroup("v1", new BpGrouping()));
  }

  // ======================================================================
  // GET GROUPS — SUCCESS
  // ======================================================================
  @Test
  void testGetBusinessProcessGroups_success() throws Exception {
    BusinessProcessGroups groups = new BusinessProcessGroups();
    groups.setGroups(List.of());
    groups.setUngrouped(List.of());

    stubFor(
        get(urlEqualTo("/versions/candidates/v2/business-process-groups"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(groups))));

    BusinessProcessGroups response = steps.getBusinessProcessGroups("v2");

    assertThat(response).isNotNull();
    assertThat(response.getGroups()).isEmpty();
    assertThat(response.getUngrouped()).isEmpty();
  }

  // ======================================================================
  // GET GROUPS — NOT FOUND
  // ======================================================================
  @Test
  void testGetBusinessProcessGroups_notFound() {
    stubFor(
        get(urlEqualTo("/versions/candidates/v2/business-process-groups"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NOT_FOUND)));

    assertThrows(AssertionError.class, () -> steps.getBusinessProcessGroups("v2"));
  }

  // ======================================================================
  // GET GROUPS — INVALID JSON
  // ======================================================================
  @Test
  void testGetBusinessProcessGroups_invalidJson() {
    stubFor(
        get(urlEqualTo("/versions/candidates/v2/business-process-groups"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{ invalid json")));

    assertThrows(Exception.class, () -> steps.getBusinessProcessGroups("v2"));
  }
}
