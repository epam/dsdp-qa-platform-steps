package platform.qa.data.consent;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.map.*;

class EntityWithGeoTypeApiTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;

  private EntityWithGeoTypeApi api;
  private SignatureSteps signatureMock;
  private Service dataFactory;

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
  void setup() throws Exception {

    try (MockedConstruction<SignatureSteps> mocked =
        Mockito.mockConstruction(
            SignatureSteps.class,
            (mock, ctx) -> {
              Mockito.when(mock.signRequest(any())).thenReturn("SIGNED-ID");
              Mockito.when(mock.signDeleteRequest(any())).thenReturn("DEL-ID");
            })) {

      User user = new User("usr", "tok");
      user.setToken("token");
      dataFactory = new Service("http://localhost:" + wireMock.port(), user);

      api = new EntityWithGeoTypeApi(dataFactory, dataFactory, List.of());
      signatureMock = mocked.constructed().get(0);

      Field field = EntityWithGeoTypeApi.class.getDeclaredField("signatureSteps");
      field.setAccessible(true);
      field.set(api, signatureMock);
    }
  }

  @Test
  void testGetLocation_success() throws Exception {
    EntityGeoTypeResponse respObj = new EntityGeoTypeResponse();
    respObj.setEntityId("LOC1");

    stubFor(
        get(urlEqualTo("/entity-with-geo-type/LOC1"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(respObj))));

    EntityGeoTypeResponse result = api.getLocation("LOC1");
    assertThat(result.getEntityId()).isEqualTo("LOC1");
  }

  @Test
  void testCreateLocation_singlePoint_success() {
    EntityGeoType payload = new EntityGeoType();
    payload.setId("SIGNED-ID");

    stubFor(
        post(urlEqualTo("/entity-with-geo-type/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"SIGNED-ID\"}")));

    String id = api.createLocation(payload);
    assertThat(id).isEqualTo("SIGNED-ID");
    Mockito.verify(signatureMock).signRequest(payload);
  }

  @Test
  void testCreateLocation_manyDots_success() {
    EntityGeoTypeManyDots payload = new EntityGeoTypeManyDots();
    payload.setId("SIGNED-ID");

    stubFor(
        post(urlEqualTo("/entity-with-geo-type/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"SIGNED-ID\"}")));

    String id = api.createLocation(payload);
    assertThat(id).isEqualTo("SIGNED-ID");
    Mockito.verify(signatureMock).signRequest(payload);
  }

  @Test
  void testUpdateLocation_success() {
    EntityGeoType payload = new EntityGeoType();
    payload.setId("U1");

    stubFor(
        put(urlEqualTo("/entity-with-geo-type/REC123")).willReturn(aResponse().withStatus(204)));

    api.updateLocation("REC123", payload);

    Mockito.verify(signatureMock).signRequest(payload);
  }

  @Test
  void testDeleteLocation_success() {

    stubFor(
        delete(urlEqualTo("/entity-with-geo-type/DEL123")).willReturn(aResponse().withStatus(204)));

    api.deleteLocationById("DEL123");

    Mockito.verify(signatureMock).signDeleteRequest("DEL123");
  }

  @Test
  void testGetEntityAddressLocation_success() throws Exception {
    EntityAddressLocationResponse r = new EntityAddressLocationResponse();
    r.setAddress("ADDR1");

    stubFor(
        get(urlPathEqualTo("/get-entity-address-from-table-geo-type/"))
            .withQueryParam("org", equalTo("101"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(List.of(r)))));

    List<EntityAddressLocationResponse> result = api.getEntityAddressLocation(Map.of("org", "101"));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getAddress()).isEqualTo("ADDR1");
  }

  @Test
  void testCreatePointOnMap_success() {
    CreateKatottgMap payload = new CreateKatottgMap();
    payload.setKatottgMapId("P1");

    stubFor(
        post(urlEqualTo("/katottg-map/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"SIGNED-ID\"}")));

    String id = api.createPointOnMap(payload);
    assertThat(id).isEqualTo("SIGNED-ID");
    Mockito.verify(signatureMock).signRequest(payload);
  }

  @Test
  void testRemovePointOnMap_success() {
    stubFor(delete(urlEqualTo("/katottg-map/RM1")).willReturn(aResponse().withStatus(204)));

    api.removePointOnMap("RM1");

    Mockito.verify(signatureMock).signDeleteRequest("RM1");
  }

  @Test
  void testCreateLineOnMap_success() {
    CreateKatottgMap payload = new CreateKatottgMap();

    stubFor(
        post(urlEqualTo("/katottg-map/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"SIGNED-ID\"}")));

    String id = api.createLineOnMap(payload);
    assertThat(id).isEqualTo("SIGNED-ID");
    Mockito.verify(signatureMock).signRequest(payload);
  }

  @Test
  void testCreatePolygonOnMap_success() {
    CreateKatottgMap payload = new CreateKatottgMap();

    stubFor(
        post(urlEqualTo("/katottg-map/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\": \"SIGNED-ID\"}")));

    String id = api.createPolygonOnMap(payload);
    assertThat(id).isEqualTo("SIGNED-ID");
    Mockito.verify(signatureMock).signRequest(payload);
  }
}
