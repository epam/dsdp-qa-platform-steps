package platform.qa.data.weapon;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.weapon.sc.AllOwnersByManufactureYear;

class WeaponSCTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private WeaponSC api;

  @BeforeAll
  static void startServer() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());
  }

  @AfterAll
  static void stopServer() {
    wireMock.stop();
  }

  private static List<AllOwnersByManufactureYear> getAllOwnersByManufactureYears() {
    AllOwnersByManufactureYear owner1 =
        new AllOwnersByManufactureYear("0441112233", "rifle", "Remington", "700", "Alice", 2020);

    AllOwnersByManufactureYear owner2 =
        new AllOwnersByManufactureYear("0679876543", "pistol", "Beretta", "92FS", "Bob", 2021);

    return List.of(owner1, owner2);
  }

  @BeforeEach
  void init() {
    User user = new User("usr", "tok");
    user.setToken("token123");
    Service s = new Service("http://localhost:" + wireMock.port(), user);
    api = new WeaponSC(s);
  }

  // ----------------------------------------------------------
  // TEST getOwnersBySingleManufactureYear()
  // ----------------------------------------------------------
  @Test
  void testGetOwnersBySingleManufactureYear_success() throws Exception {

    AllOwnersByManufactureYear owner =
        new AllOwnersByManufactureYear(
            "0991234567", // contacts
            "carbine", // type
            "Glock", // brand
            "G17", // model
            "John Doe", // name
            2020 // manufactureYear
            );

    List<AllOwnersByManufactureYear> list = List.of(owner);

    stubFor(
        get(urlPathEqualTo("/find-all-owners-search-manufacture-year"))
            .withQueryParam("manufactureYear", equalTo("2020"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<AllOwnersByManufactureYear> result = api.getOwnersBySingleManufactureYear(2020);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getManufactureYear()).isEqualTo(2020);
    assertThat(result.get(0).getBrand()).isEqualTo("Glock");
  }

  // ----------------------------------------------------------
  // TEST getOwnersBySeveralManufactureYears()
  // ----------------------------------------------------------
  @Test
  void testGetOwnersBySeveralManufactureYears_success() throws Exception {

    List<AllOwnersByManufactureYear> list = getAllOwnersByManufactureYears();

    stubFor(
        get(urlEqualTo(
                "/find-all-owners-search-manufacture-year?manufactureYear=2020&manufactureYear=2021"))
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<AllOwnersByManufactureYear> result =
        api.getOwnersBySeveralManufactureYears(List.of(2020, 2021));

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("Alice");
    assertThat(result.get(1).getName()).isEqualTo("Bob");
  }
}
