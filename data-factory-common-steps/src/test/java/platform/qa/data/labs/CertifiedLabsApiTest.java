package platform.qa.data.labs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.labs.*;
import platform.qa.pojo.labs.searchConditions.StaffStatusSearch;
import platform.qa.redis.JedisClient;

class CertifiedLabsApiTest {

  MockedConstruction<JedisClient> mockedJedis;
  private WireMockServer wireMock;
  private CertifiedLabsApi api;
  private Service service;
  private SignatureSteps signatureMock;

  @BeforeEach
  void setup() throws Exception {

    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    // user init
    User user = new User("usr", "pwd");
    user.setToken("token");
    service = new Service("http://localhost:" + wireMock.port(), user);

    // Mock Redis/Jedis constructor
    mockedJedis =
        Mockito.mockConstruction(
            JedisClient.class,
            (mock, ctx) -> {
              when(mock.hset(anyString(), anyString(), anyString())).thenReturn(1111L);

              doNothing().when(mock).close();
            });

    // Mock SignatureSteps
    signatureMock = Mockito.mock(SignatureSteps.class);
    when(signatureMock.signRequest(any())).thenReturn("SIGN123");
    when(signatureMock.signDeleteRequest(any())).thenReturn("DEL123");

    api = new CertifiedLabsApi(service, service, List.of(new Redis("localhost", "pwd")));

    // Inject signatureMock
    Field f = CertifiedLabsApi.class.getDeclaredField("signatureSteps");
    f.setAccessible(true);
    f.set(api, signatureMock);
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
    mockedJedis.close();
  }

  // ---------------------------
  // GET laboratory
  // ---------------------------
  @Test
  void testGetLaboratory_success() {
    stubFor(
        get(urlEqualTo("/laboratory/L1"))
            .willReturn(okJson("{\"laboratoryId\":\"L1\",\"name\":\"TestLab\"}")));

    Laboratory lab = api.getToLaboratory("L1");

    assertThat(lab.getLaboratoryId()).isEqualTo("L1");
    assertThat(lab.getName()).isEqualTo("TestLab");
  }

  // ---------------------------
  // GET staff
  // ---------------------------
  @Test
  void testGetStaff_success() {
    stubFor(
        get(urlEqualTo("/staff/S1"))
            .willReturn(okJson("{\"staffId\":\"S1\",\"fullName\":\"Ivan Ivanov\"}")));

    Staff s = api.getToStaff("S1");

    assertThat(s.getStaffId()).isEqualTo("S1");
    assertThat(s.getFullName()).isEqualTo("Ivan Ivanov");
  }

  // ---------------------------
  // createLaboratory
  // ---------------------------
  @Test
  void testCreateLaboratory_success() {
    stubFor(
        post(urlEqualTo("/laboratory/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"LAB123\"}")));

    Laboratory lab = new Laboratory();
    String id = api.createLaboratory(lab);

    assertThat(id).isEqualTo("LAB123");
  }

  // ---------------------------
  // register()
  // ---------------------------
  @Test
  void testRegister_success() {
    stubFor(
        post(urlEqualTo("/registration/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"REG55\"}")));

    Registration reg = new Registration();
    String id = api.register(reg);

    assertThat(id).isEqualTo("REG55");
  }

  // ---------------------------
  // deleteLaboratory
  // ---------------------------
  @Test
  void testDeleteLaboratory_success() {
    stubFor(delete(urlEqualTo("/laboratory/L88")).willReturn(aResponse().withStatus(204)));

    api.deleteLaboratory("L88");

    verify(deleteRequestedFor(urlEqualTo("/laboratory/L88")));
  }

  // ---------------------------
  // deleteRegistration
  // ---------------------------
  @Test
  void testDeleteRegistration_success() {
    stubFor(delete(urlEqualTo("/registration/R22")).willReturn(aResponse().withStatus(204)));

    api.deleteRegistration("R22");

    verify(deleteRequestedFor(urlEqualTo("/registration/R22")));
  }

  // ---------------------------
  // createStaff
  // ---------------------------
  @Test
  void testCreateStaff_success() {
    stubFor(
        post(urlEqualTo("/staff/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"ST999\"}")));

    Staff s = new Staff();
    String id = api.createStaff(s);

    assertThat(id).isEqualTo("ST999");
  }

  // ---------------------------
  // getResearch
  // ---------------------------
  @Test
  void testGetResearch_success() {
    stubFor(
        get(urlEqualTo("/research/R1"))
            .willReturn(okJson("{\"researchId\":\"R1\",\"researchType\":\"BIO\"}")));

    Research r = api.getResearchById("R1");

    assertThat(r.getResearchId()).isEqualTo("R1");
    assertThat(r.getResearchType()).isEqualTo("BIO");
  }

  // ---------------------------
  // getStaffStatusById
  // ---------------------------
  @Test
  void testGetStaffStatus_success() {
    stubFor(
        get(urlEqualTo("/staff-status/S9"))
            .willReturn(okJson("{\"staffStatusId\":\"S9\",\"name\":\"Active\"}")));

    StaffStatusSearch status = api.getStaffStatusById("S9");

    assertThat(status.getStaffStatusId()).isEqualTo("S9");
    assertThat(status.getName()).isEqualTo("Active");
  }
}
