package platform.qa.data.weapon;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.objenesis.ObjenesisStd;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.weapon.Brand;
import platform.qa.pojo.weapon.Model;
import platform.qa.pojo.weapon.Type;
import platform.qa.pojo.weapon.Weapon;

class WeaponApiTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private WeaponApi api;
  private SignatureSteps signatureStepsMock;
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
  void init() {
    User user = new User("usr", "123");
    user.setToken("token123");
    dataFactory = new Service("http://localhost:" + wireMock.port(), user);

    ObjenesisStd obj = new ObjenesisStd();
    api = obj.newInstance(WeaponApi.class);

    InjectField("dataFactory", dataFactory);

    signatureStepsMock = Mockito.mock(SignatureSteps.class);
    Mockito.when(signatureStepsMock.signRequest(Mockito.any())).thenReturn("SIGNED-ID");

    InjectField("signatureSteps", signatureStepsMock);
  }

  private void InjectField(String name, Object value) {
    try {
      var f = WeaponApi.class.getDeclaredField(name);
      f.setAccessible(true);
      f.set(api, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // -------------------------------------------------------------------------
  // TEST createWeapon()
  // -------------------------------------------------------------------------
  @Test
  void testCreateWeapon_success() {
    Weapon w = new Weapon();
    w.setWeaponId("SIGNED-ID");

    stubFor(
        post(urlEqualTo("/weapon/"))
            .willReturn(
                aResponse()
                    .withStatus(SC_CREATED)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"SIGNED-ID\"}")));

    String id = api.createWeapon(w);

    assertThat(id).isEqualTo("SIGNED-ID");
    Mockito.verify(signatureStepsMock).signRequest(w);
  }

  // -------------------------------------------------------------------------
  // TEST getWeaponById()
  // -------------------------------------------------------------------------
  @Test
  void testGetWeaponById_success() throws Exception {
    Weapon w = new Weapon();
    w.setWeaponId("42");

    stubFor(
        get(urlEqualTo("/weapon/42"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(w))));

    Weapon resp = api.getWeaponById("42");

    assertThat(resp.getWeaponId()).isEqualTo("42");
  }

  // -------------------------------------------------------------------------
  // TEST getWeaponTypeById()
  // -------------------------------------------------------------------------
  @Test
  void testGetWeaponTypeById_success() throws Exception {
    Type t = new Type();
    t.setTypeId("T1");

    stubFor(
        get(urlEqualTo("/type/T1"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(t))));

    Type resp = api.getWeaponTypeById("T1");
    assertThat(resp.getTypeId()).isEqualTo("T1");
  }

  // -------------------------------------------------------------------------
  // TEST getWeaponModelById()
  // -------------------------------------------------------------------------
  @Test
  void testGetWeaponModelById_success() throws Exception {
    Model m = new Model();
    m.setModelId("M9");

    stubFor(
        get(urlEqualTo("/model/M9"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(m))));

    Model resp = api.getWeaponModelById("M9");
    assertThat(resp.getModelId()).isEqualTo("M9");
  }

  // -------------------------------------------------------------------------
  // TEST getWeaponBrandById()
  // -------------------------------------------------------------------------
  @Test
  void testGetWeaponBrandById_success() throws Exception {
    Brand b = new Brand();
    b.setBrandId("BR77");

    stubFor(
        get(urlEqualTo("/brand/BR77"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(b))));

    Brand resp = api.getWeaponBrandById("BR77");
    assertThat(resp.getBrandId()).isEqualTo("BR77");
  }
}
