package platform.qa.data.rls;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.rls.RlsModel;
import platform.qa.redis.JedisClient;

class RlsModelApiTest {

  MockedConstruction<JedisClient> jedisMock;
  private WireMockServer wireMock;
  private RlsModelApi api;
  private SignatureSteps signatureMock;

  @BeforeEach
  void setup() throws Exception {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    jedisMock =
        Mockito.mockConstruction(
            JedisClient.class,
            (mock, ctx) -> {
              doNothing().when(mock).close();

              Mockito.doAnswer(inv -> null).when(mock).hset(anyString(), anyString(), anyString());

              Mockito.doAnswer(inv -> null).when(mock).set(anyString(), anyString());
            });

    User user = new User("usr", "pwd");
    user.setToken("token");
    Service service = new Service("http://localhost:" + wireMock.port(), user);

    signatureMock = Mockito.mock(SignatureSteps.class);
    when(signatureMock.signRequest(Mockito.any())).thenReturn("SIGN123");
    when(signatureMock.signDeleteRequest(Mockito.any())).thenReturn("DEL123");

    api = new RlsModelApi(service, service, List.of(new Redis("localhost", "pwd")));

    // inject signature mock
    var f = RlsModelApi.class.getDeclaredField("signatureSteps");
    f.setAccessible(true);
    f.set(api, signatureMock);
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
    jedisMock.close();
  }

  // ----------------------------
  // CREATE 201
  // ----------------------------
  @Test
  void testCreateRlsModel_success() {
    stubFor(post(urlEqualTo("/rls-model/")).willReturn(created().withBody("{\"id\":\"RLS123\"}")));

    RlsModel model = new RlsModel();
    String id = api.createRlsModel(model);

    assertThat(id).isEqualTo("RLS123");
    assertThat(model.getRlsModelId()).isEqualTo("RLS123");
  }

  // ----------------------------
  // CREATE NEGATIVE 403
  // ----------------------------
  @Test
  void testCreateRlsModel_negative403() {
    stubFor(
        post(urlEqualTo("/rls-model/"))
            .willReturn(forbidden().withBody("{\"error\":\"Forbidden\"}")));

    RlsModel model = new RlsModel();

    var response = api.createRlsModelForNegativePath(model);

    assertThat(response.statusCode()).isEqualTo(403);
    assertThat(response.jsonPath().getString("error")).isEqualTo("Forbidden");
  }

  // ----------------------------
  // GET 200
  // ----------------------------
  @Test
  void testGetRlsModel_success() {
    stubFor(
        get(urlEqualTo("/rls-model/RLS1"))
            .willReturn(okJson("{\"rlsModelId\":\"RLS1\",\"rlsModelName\":\"TestModel\"}")));

    RlsModel model = api.getRlsModelById("RLS1");

    assertThat(model.getRlsModelId()).isEqualTo("RLS1");
    assertThat(model.getRlsModelName()).isEqualTo("TestModel");
  }

  // ----------------------------
  // GET NEGATIVE 403
  // ----------------------------
  @Test
  void testGetRlsModel_negative403() {
    stubFor(
        get(urlEqualTo("/rls-model/RLS_BAD"))
            .willReturn(forbidden().withBody("{\"error\":\"Forbidden\"}")));

    var response = api.getRlsModelByIdForNegativePath("RLS_BAD");

    assertThat(response.statusCode()).isEqualTo(403);
  }

  // ----------------------------
  // UPDATE 204
  // ----------------------------
  @Test
  void testUpdateRlsModel_success() {
    stubFor(put(urlEqualTo("/rls-model/RLS2")).willReturn(noContent()));

    RlsModel patch = new RlsModel();

    api.updateRlsModelById("RLS2", patch);

    verify(putRequestedFor(urlEqualTo("/rls-model/RLS2")));
  }

  // ----------------------------
  // UPDATE NEGATIVE 403
  // ----------------------------
  @Test
  void testUpdateRlsModel_negative403() {
    stubFor(
        put(urlEqualTo("/rls-model/RLS_ERR"))
            .willReturn(forbidden().withBody("{\"error\":\"Forbidden\"}")));

    RlsModel patch = new RlsModel();

    var resp = api.updateRlsModelByIdForNegativePath("RLS_ERR", patch);

    assertThat(resp.statusCode()).isEqualTo(403);
  }

  // ----------------------------
  // DELETE 204
  // ----------------------------
  @Test
  void testDeleteRlsModel_success() {
    stubFor(delete(urlEqualTo("/rls-model/RLS_DEL")).willReturn(noContent()));

    api.deleteRlsModelById("RLS_DEL");

    verify(deleteRequestedFor(urlEqualTo("/rls-model/RLS_DEL")));
  }
}
