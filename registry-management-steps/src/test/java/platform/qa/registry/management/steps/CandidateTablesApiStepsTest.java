package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.response.table.Table;
import platform.qa.registry.management.dto.response.table.TableData;

class CandidateTablesApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private CandidateTablesApiSteps steps;

  @BeforeAll
  static void startServer() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());
  }

  @AfterAll
  static void stopServer() {
    wireMock.stop();
  }

  @BeforeEach
  void setup() {
    Service service =
        new Service("http://localhost:" + wireMock.port(), new User("usr", "token123"));
    steps = new CandidateTablesApiSteps(service);
  }

  // ============================================================================
  // GET TABLE LIST — SUCCESS
  // ============================================================================
  @Test
  void testGetTablesList_success() throws Exception {
    List<Table> tables = List.of(new Table("users", "Users table", false));

    stubFor(
        get(urlEqualTo("/versions/candidates/v1/tables"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(tables))));

    List<Table> resp = steps.getTablesList("v1");

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getName()).isEqualTo("users");
  }

  // ============================================================================
  // GET SPECIFIC TABLE — SUCCESS
  // ============================================================================
  @Test
  void testGetSpecificTableFullDetails_success() throws Exception {
    TableData td =
        TableData.builder()
            .name("users")
            .description("desc")
            .objectReference(false)
            .columns(Map.of("id", Map.of("type", "uuid")))
            .primaryKey(Map.of("id", "uuid"))
            .foreignKeys(Map.of())
            .uniqueConstraints(Map.of())
            .indices(Map.of())
            .build();

    stubFor(
        get(urlEqualTo("/versions/candidates/v1/tables/users"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(td))));

    TableData resp = steps.getSpecificTableFullDetails("v1", "users");

    assertThat(resp.getName()).isEqualTo("users");
    assertThat(resp.getColumns()).containsKey("id");
  }

  // ============================================================================
  // UPDATE TABLES — SUCCESS (XML)
  // ============================================================================
  @Test
  void testUpdateTables_success() {
    stubFor(
        put(urlEqualTo("/versions/candidates/v1/data-model/tables"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody("\"UPDATED\"")));

    String response = steps.updateTables("v1", "<xml/>");

    assertThat(response).isEqualTo("\"UPDATED\"");
  }
}
