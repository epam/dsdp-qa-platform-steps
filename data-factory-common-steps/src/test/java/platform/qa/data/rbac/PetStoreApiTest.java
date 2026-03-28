package platform.qa.data.rbac;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import platform.qa.pojo.petstore.PetStore;
import platform.qa.redis.JedisClient;

class PetStoreApiTest {

  MockedConstruction<JedisClient> jedisMockConstruction;
  private WireMockServer wireMock;
  private PetStoreApi api;
  private SignatureSteps signatureMock;
  private Service service;

  @BeforeEach
  void setup() throws Exception {
    jedisMockConstruction =
        Mockito.mockConstruction(
            JedisClient.class,
            (mock, ctx) -> {
              // nothing to do here
            });
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("usr", "pwd");
    user.setToken("token");
    service = new Service("http://localhost:" + wireMock.port(), user);

    signatureMock = Mockito.mock(SignatureSteps.class);
    when(signatureMock.signRequest(any())).thenReturn("SIGN123");
    when(signatureMock.signDeleteRequest(any())).thenReturn("SIGN_DEL");

    api = new PetStoreApi(service, service, List.of(new Redis("localhost", "pwd")));

    Field f = PetStoreApi.class.getDeclaredField("signatureSteps");
    f.setAccessible(true);
    f.set(api, signatureMock);
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
    jedisMockConstruction.close();
  }

  // --------------------------------------------------
  // GET
  // --------------------------------------------------
  @Test
  void testGetPetStoreById_success() {
    stubFor(
        get(urlEqualTo("/pet-store/PS1"))
            .willReturn(okJson("{\"petStoreId\":\"PS1\",\"name\":\"Zoo Market\"}")));

    PetStore ps = api.getPetStoreById("PS1");

    assertThat(ps).isNotNull();
    assertThat(ps.getPetStoreId()).isEqualTo("PS1");
    assertThat(ps.getName()).isEqualTo("Zoo Market");
  }

  // --------------------------------------------------
  // POST
  // --------------------------------------------------
  @Test
  void testCreatePetStore_success() {
    stubFor(
        post(urlEqualTo("/pet-store/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"PS_NEW\"}")));

    PetStore payload = new PetStore();
    payload.setName("Test Store");

    String id = api.createPetStore(payload);

    assertThat(id).isEqualTo("PS_NEW");
  }

  // --------------------------------------------------
  // PUT
  // --------------------------------------------------
  @Test
  void testUpdatePetStoreById_success() {
    stubFor(put(urlEqualTo("/pet-store/PS2")).willReturn(aResponse().withStatus(204)));

    PetStore patch = new PetStore();
    patch.setName("Updated");

    api.updatePetStoreById("PS2", patch);

    verify(putRequestedFor(urlEqualTo("/pet-store/PS2")));
  }

  // --------------------------------------------------
  // DELETE
  // --------------------------------------------------
  @Test
  void testDeletePetStoreById_success() {
    stubFor(delete(urlEqualTo("/pet-store/PS3")).willReturn(noContent()));

    api.deletePetStoreById("PS3");

    verify(deleteRequestedFor(urlEqualTo("/pet-store/PS3")));
  }
}
