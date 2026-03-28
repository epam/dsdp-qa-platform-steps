package platform.qa.data.rbac;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.petstore.sc.PetStoreByName;

class PetStoreSCTest {

  private WireMockServer wireMock;
  private PetStoreSC api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("usr", "pwd");
    user.setToken("token");
    Service service = new Service("http://localhost:" + wireMock.port(), user);
    api = new PetStoreSC(service);
  }

  @AfterEach
  void teardown() {
    wireMock.stop();
  }

  // -----------------------------
  // POSITIVE TEST
  // -----------------------------
  @Test
  void testGetPetStoresByName_success() {
    stubFor(
        get(urlPathEqualTo("/rbac-pet-store-by-name/"))
            .withQueryParam("name", equalTo("Fluffy"))
            .willReturn(
                okJson(
                    "[{"
                        + "\"name\":\"Fluffy\","
                        + "\"owner\":\"Alice\","
                        + "\"serialNumber\":\"SN123\""
                        + "}]")));

    List<PetStoreByName> result = api.getPetStoresByName("Fluffy");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Fluffy");
    assertThat(result.get(0).getOwner()).isEqualTo("Alice");
    assertThat(result.get(0).getSerialNumber()).isEqualTo("SN123");
  }

  // -----------------------------
  // NEGATIVE TEST (403)
  // -----------------------------
  @Test
  void testGetPetStoresByName_negative403() {
    stubFor(
        get(urlPathEqualTo("/rbac-pet-store-by-name/"))
            .withQueryParam("name", equalTo("Hacker"))
            .willReturn(forbidden().withBody("{\"error\":\"Forbidden\"}")));

    var response = api.getPetStoresByNameNegativePath("Hacker");

    assertThat(response.statusCode()).isEqualTo(403);
    assertThat(response.jsonPath().getString("error")).isEqualTo("Forbidden");
  }
}
