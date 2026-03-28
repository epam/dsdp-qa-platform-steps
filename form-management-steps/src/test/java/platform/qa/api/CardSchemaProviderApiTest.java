package platform.qa.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;

class CardSchemaProviderApiTest {

  private WireMockServer wireMock;
  private CardSchemaProviderApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("usr", "pwd");
    user.setToken("token123");

    Service service = new Service("http://localhost:" + wireMock.port(), user);

    api = new CardSchemaProviderApi(service);
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  // ---------------------------------------------------------
  // GET CARD BY KEY
  // ---------------------------------------------------------

  @Test
  void testGetCardByKey_success() {
    stubFor(
        get(urlEqualTo("/api/cards/CardA"))
            .willReturn(
                okJson(
                    """
                {"id":"CardA","type":"card","version":2}
                """)));

    var result = api.getCardByKey("CardA");

    assertThat(result)
        .containsEntry("id", "CardA")
        .containsEntry("type", "card")
        .containsEntry("version", 2);
  }

  // ---------------------------------------------------------
  // RAW RESPONSE GET CARD
  // ---------------------------------------------------------

  @Test
  void testSendGetCardByKey_success() {
    stubFor(get(urlEqualTo("/api/cards/TestCard")).willReturn(okJson("{\"id\":\"TestCard\"}")));

    Response resp = api.sendGetCardByKey("TestCard");

    assertThat(resp.statusCode()).isEqualTo(200);
    assertThat(resp.jsonPath().getString("id")).isEqualTo("TestCard");
  }

  // ---------------------------------------------------------
  // GET CARDS META
  // ---------------------------------------------------------

  @Test
  void testGetCardsMeta_success() {
    stubFor(
        get(urlEqualTo("/api/cards/meta"))
            .willReturn(
                okJson(
                    """
                [
                  {"id":"card-1","name":"Card 1"},
                  {"id":"card-2","name":"Card 2"}
                ]
                """)));

    List<Map<String, String>> result = api.getCardsMeta();

    assertThat(result).hasSize(2);
    assertThat(result.get(0)).containsEntry("id", "card-1");
    assertThat(result.get(1)).containsEntry("name", "Card 2");
  }

  @Test
  void testSendGetCardsMeta_success() {
    stubFor(get(urlEqualTo("/api/cards/meta")).willReturn(okJson("[{\"id\":\"X\"}]")));

    Response resp = api.sendGetCardsMeta();

    assertThat(resp.statusCode()).isEqualTo(200);
    assertThat(resp.jsonPath().getList("$")).hasSize(1);
  }

  // ---------------------------------------------------------
  // UNAUTHORIZED TESTS
  // ---------------------------------------------------------

  @Test
  void testSendGetCardWithoutAuth_401() {
    stubFor(get(urlEqualTo("/api/cards/NoAuth")).willReturn(unauthorized()));

    Response resp = api.sendGetCardWithoutAuth("NoAuth");

    assertThat(resp.statusCode()).isEqualTo(401);
  }

  @Test
  void testSendGetCardsMetaWithoutAuth_403() {
    stubFor(get(urlEqualTo("/api/cards/meta")).willReturn(forbidden()));

    Response resp = api.sendGetCardsMetaWithoutAuth();

    assertThat(resp.statusCode()).isEqualTo(403);
  }

  // ---------------------------------------------------------
  // CUSTOM TOKEN TESTS
  // ---------------------------------------------------------

  @Test
  void testSendGetCardWithCustomToken_success() {
    stubFor(
        get(urlEqualTo("/api/cards/SecureCard"))
            .withHeader("X-Access-Token", equalTo("CUSTOM"))
            .willReturn(okJson("{\"id\":\"SecureCard\"}")));

    Response resp = api.sendGetCardWithCustomToken("SecureCard", "CUSTOM");

    assertThat(resp.statusCode()).isEqualTo(200);
    assertThat(resp.jsonPath().getString("id")).isEqualTo("SecureCard");

    verify(
        getRequestedFor(urlEqualTo("/api/cards/SecureCard"))
            .withHeader("X-Access-Token", equalTo("CUSTOM")));
  }

  @Test
  void testSendGetCardsMetaWithCustomToken_success() {
    stubFor(
        get(urlEqualTo("/api/cards/meta"))
            .withHeader("X-Access-Token", equalTo("ADMIN"))
            .willReturn(okJson("[{\"id\":\"meta1\"}]")));

    Response resp = api.sendGetCardsMetaWithCustomToken("ADMIN");

    assertThat(resp.statusCode()).isEqualTo(200);
    assertThat(resp.jsonPath().getString("[0].id")).isEqualTo("meta1");

    verify(
        getRequestedFor(urlEqualTo("/api/cards/meta"))
            .withHeader("X-Access-Token", equalTo("ADMIN")));
  }
}
