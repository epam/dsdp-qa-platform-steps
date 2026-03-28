package platform.qa.data.delegation;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.delegation.DelegationAuthorizedPerson;
import platform.qa.pojo.delegation.DelegationLicense;
import platform.qa.pojo.delegation.DelegationOrganization;

class DelegationSCTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private DelegationSC sc;
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
  void init() {
    User user = new User("usr", "pwd");
    user.setToken("token");
    service = new Service("http://localhost:" + wireMock.port(), user);
    sc = new DelegationSC(service);
  }

  // ------------------------------------------------------------------------
  //  getAuthPersonsByEdrpou
  // ------------------------------------------------------------------------

  @Test
  void testGetAuthPersonsByEdrpou_success() throws Exception {
    DelegationAuthorizedPerson p1 = new DelegationAuthorizedPerson();
    p1.setId("A1");

    stubFor(
        get(urlPathEqualTo("/get-delegation-authorized-persons-by-edrpou/"))
            .withQueryParam("organizationEdrpou", equalTo("12345678"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(List.of(p1)))));

    List<DelegationAuthorizedPerson> resp = sc.getAuthPersonsByEdrpou("12345678");

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getId()).isEqualTo("A1");
  }

  @Test
  void testGetAuthPersonsByEdrpou_nullInput_returnsNull() {
    assertThat(sc.getAuthPersonsByEdrpou(null)).isNull();
  }

  // ------------------------------------------------------------------------
  //  isDelegationAuthPersonExist
  // ------------------------------------------------------------------------

  @Test
  void testIsDelegationAuthPersonExist_success() {
    // API returns "cnt": "[1]"  → method strips brackets → "1"
    stubFor(
        get(urlPathEqualTo("/is-delegation-authorized-person-exists/"))
            .withQueryParam("organizationEdrpou", equalTo("111"))
            .withQueryParam("edrpou", equalTo("222"))
            .withQueryParam("drfo", equalTo("333"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"cnt\":\"[1]\"}")));

    String result = sc.isDelegationAuthPersonExist("111", "222", "333");

    assertThat(result).isEqualTo("1");
  }

  @Test
  void testIsDelegationAuthPersonExist_nullParams_returnsNull() {
    assertThat(sc.isDelegationAuthPersonExist(null, "x", "y")).isNull();
    assertThat(sc.isDelegationAuthPersonExist("x", null, "y")).isNull();
    assertThat(sc.isDelegationAuthPersonExist("x", "y", null)).isNull();
  }

  // ------------------------------------------------------------------------
  //  getAllActiveLicensesForOrganization
  // ------------------------------------------------------------------------

  @Test
  void testGetAllActiveLicensesForOrganization_success() throws Exception {
    DelegationLicense lic = new DelegationLicense();
    lic.setId("L1");

    stubFor(
        get(urlPathEqualTo("/get-all-active-licenses-for-organization/"))
            .withQueryParam("organizationEdrpou", equalTo("5555"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(List.of(lic)))));

    List<DelegationLicense> resp = sc.getAllActiveLicensesForOrganization("5555");

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getId()).isEqualTo("L1");
  }

  @Test
  void testGetAllActiveLicensesForOrganization_nullInput_returnsNull() {
    assertThat(sc.getAllActiveLicensesForOrganization(null)).isNull();
  }

  // ------------------------------------------------------------------------
  //  getDelegationOrganizationByEdprou
  // ------------------------------------------------------------------------

  @Test
  void testGetDelegationOrganizationByEdprou_success() throws Exception {
    DelegationOrganization org = new DelegationOrganization();
    org.setId("ORG-999");

    stubFor(
        get(urlPathEqualTo("/get-delegation-organization-by-edrpou/"))
            .withQueryParam("edrpou", equalTo("99999999"))
            .withQueryParam("limit", equalTo("100"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(List.of(org)))));

    List<DelegationOrganization> resp = sc.getDelegationOrganizationByEdprou("99999999");

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getId()).isEqualTo("ORG-999");
  }

  @Test
  void testGetDelegationOrganizationByEdprou_nullInput_returnsNull() {
    assertThat(sc.getDelegationOrganizationByEdprou(null)).isNull();
  }
}
