package platform.qa.data.delegation;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.delegation.DelegationAuthorizedPerson;
import platform.qa.pojo.delegation.DelegationLicense;
import platform.qa.pojo.delegation.DelegationOrganization;

class DelegationApiTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private DelegationApi api;
  private SignatureSteps signatureMock;
  private Service dataFactory;

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
  void setup() throws Exception {

    try (MockedConstruction<SignatureSteps> mocked =
        Mockito.mockConstruction(
            SignatureSteps.class,
            (mock, context) -> {
              Mockito.when(mock.signRequest(any())).thenReturn("SIGNED-ID");
              Mockito.when(mock.signDeleteRequest(any())).thenReturn("DEL-ID");
            })) {

      User user = new User("usr", "123");
      user.setToken("token");
      dataFactory = new Service("http://localhost:" + wireMock.port(), user);

      api = new DelegationApi(dataFactory, dataFactory, List.of());

      signatureMock = mocked.constructed().get(0);

      Field f = DelegationApi.class.getDeclaredField("signatureSteps");
      f.setAccessible(true);
      f.set(api, signatureMock);
    }
  }

  @Test
  void testGetOrganization_success() throws JsonProcessingException {
    DelegationOrganization org = new DelegationOrganization();
    org.setId("ORG1");

    stubFor(
        get(urlEqualTo("/delegation-organization/ORG1"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(org))));

    DelegationOrganization resp = api.getOrganization("ORG1");

    assertThat(resp.getId()).isEqualTo("ORG1");
  }

  @Test
  void testCreateOrganization_success() {
    DelegationOrganization org = new DelegationOrganization();
    org.setId("SIGNED-ID");

    // HTTP mock
    stubFor(
        post(urlEqualTo("/delegation-organization/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"SIGNED-ID\"}")));

    String id = api.createOrganization(org);

    assertThat(id).isEqualTo("SIGNED-ID");
    Mockito.verify(signatureMock).signRequest(org);
  }

  @Test
  void testDeleteOrganization_success() {
    stubFor(
        delete(urlEqualTo("/delegation-organization/ORG5"))
            .willReturn(aResponse().withStatus(204)));

    api.deleteOrganization("ORG5");

    Mockito.verify(signatureMock).signDeleteRequest("ORG5");
  }

  @Test
  void testGetLicense_success() throws JsonProcessingException {
    DelegationLicense lic = new DelegationLicense();
    lic.setId("L77");

    stubFor(
        get(urlEqualTo("/delegation-license/L77"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(lic))));

    DelegationLicense resp = api.getLicense("L77");

    assertThat(resp.getId()).isEqualTo("L77");
  }

  @Test
  void testCreateLicense_success() {
    DelegationLicense lic = new DelegationLicense();

    stubFor(
        post(urlEqualTo("/delegation-license/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"SIGNED-ID\"}")));

    String id = api.createLicense(lic);

    assertThat(id).isEqualTo("SIGNED-ID");
    Mockito.verify(signatureMock).signRequest(lic);
  }

  @Test
  void testDeleteLicense_success() {
    stubFor(delete(urlEqualTo("/delegation-license/L11")).willReturn(aResponse().withStatus(204)));

    api.deleteLicense("L11");

    Mockito.verify(signatureMock).signDeleteRequest("L11");
  }

  @Test
  void testGetAuthPerson_success() throws Exception {
    DelegationAuthorizedPerson p = new DelegationAuthorizedPerson();
    p.setId("P1");

    stubFor(
        get(urlEqualTo("/delegation-authorized-person/P1"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(p))));

    DelegationAuthorizedPerson resp = api.getAuthPerson("P1");

    assertThat(resp.getId()).isEqualTo("P1");
  }

  @Test
  void testCreateAuthPerson_success() {
    DelegationAuthorizedPerson p = new DelegationAuthorizedPerson();

    stubFor(
        post(urlEqualTo("/delegation-authorized-person/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"SIGNED-ID\"}")));

    String id = api.createAuthPerson(p);

    assertThat(id).isEqualTo("SIGNED-ID");
    Mockito.verify(signatureMock).signRequest(p);
  }
}
