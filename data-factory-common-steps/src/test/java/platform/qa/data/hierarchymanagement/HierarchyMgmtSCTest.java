package platform.qa.data.hierarchymanagement;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.*;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.hierarchymanagement.DocumentRegistration;
import platform.qa.pojo.hierarchymanagement.Unit;

class HierarchyMgmtSCTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private HierarchyMgmtSC api;
  private Service service;

  @BeforeAll
  static void start() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());
  }

  @AfterAll
  static void stop() {
    wireMock.stop();
  }

  @BeforeEach
  void setup() {
    User user = new User("usr", "tkn");
    user.setToken("token");
    service = new Service("http://localhost:" + wireMock.port(), user);
    api = new HierarchyMgmtSC(service);
  }

  // ---------- TEST getAllUnits() + getUnits() ----------
  @Test
  void testGetAllUnits_success() throws Exception {
    Unit u1 = new Unit();
    u1.setId("U1");
    Unit u2 = new Unit();
    u2.setId("U2");

    List<Unit> units = List.of(u1, u2);

    stubFor(
        get(urlPathEqualTo("/find-all-units/"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(units))));

    List<Unit> resp = api.getAllUnits();

    assertThat(resp).hasSize(2);
    assertThat(resp.get(0).getId()).isEqualTo("U1");
    assertThat(resp.get(1).getId()).isEqualTo("U2");
  }

  @Test
  void testGetUnits_withParams() throws Exception {
    Unit u1 = new Unit();
    u1.setId("FILT-U1");

    List<Unit> units = List.of(u1);

    stubFor(
        get(urlPathEqualTo("/find-all-units/"))
            .withQueryParam("code", equalTo("101"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(units))));

    Map<String, String> params = Map.of("code", "101");

    List<Unit> resp = api.getUnits(params);

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getId()).isEqualTo("FILT-U1");
  }

  // ---------- TEST getAllDocuments() + getDocuments() ----------
  @Test
  void testGetAllDocuments_success() throws Exception {
    DocumentRegistration d1 = new DocumentRegistration();
    d1.setId("D1");
    DocumentRegistration d2 = new DocumentRegistration();
    d2.setId("D2");

    List<DocumentRegistration> docs = List.of(d1, d2);

    stubFor(
        get(urlPathEqualTo("/find-all-documents/"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(docs))));

    List<DocumentRegistration> resp = api.getAllDocuments();

    assertThat(resp).hasSize(2);
    assertThat(resp.get(0).getId()).isEqualTo("D1");
    assertThat(resp.get(1).getId()).isEqualTo("D2");
  }

  @Test
  void testGetDocuments_withParams() throws Exception {
    DocumentRegistration d = new DocumentRegistration();
    d.setId("XDOC");

    List<DocumentRegistration> docs = List.of(d);

    stubFor(
        get(urlPathEqualTo("/find-all-documents/"))
            .withQueryParam("unitId", equalTo("7"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(docs))));

    Map<String, String> params = Map.of("unitId", "7");

    List<DocumentRegistration> resp = api.getDocuments(params);

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getId()).isEqualTo("XDOC");
  }
}
