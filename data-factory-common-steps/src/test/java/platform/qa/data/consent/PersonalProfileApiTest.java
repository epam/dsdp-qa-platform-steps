package platform.qa.data.consent;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Service;
import platform.qa.entities.Subject;
import platform.qa.entities.User;
import platform.qa.pojo.consent.PersonProfile;

class PersonalProfileApiTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;
  private PersonalProfileApi api;
  private SignatureSteps signatureMock;
  private Service dataFactory;

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

  @BeforeEach
  void setup() throws Exception {

    try (MockedConstruction<SignatureSteps> mocked =
        mockConstruction(
            SignatureSteps.class,
            (mock, context) -> {
              when(mock.signDeleteRequest(any())).thenReturn("DEL-ID");
              when(mock.signRequest(any())).thenReturn("SIGN-ID");
            })) {

      User user = new User("usr", "tok");
      user.setToken("token");
      dataFactory = new Service("http://localhost:" + wireMock.port(), user);

      api = new PersonalProfileApi(dataFactory, dataFactory, List.of());

      signatureMock = mocked.constructed().get(0);

      Field f = PersonalProfileApi.class.getDeclaredField("signatureSteps");
      f.setAccessible(true);
      f.set(api, signatureMock);
    }
  }

  // ============================
  // GET PERSON BY ID
  // ============================

  @Test
  void testGetPersonById_success() throws JsonProcessingException {
    Subject subj = new Subject();
    subj.setSubjectId("P123");

    stubFor(
        get(urlEqualTo("/person-profile/P123"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(subj))));

    Subject result = api.getPersonById("P123");

    assertThat(result).isNotNull();
    assertThat(result.getSubjectId()).isEqualTo("P123");
  }

  // ============================
  // GET BY LAST NAME
  // ============================

  @Test
  void testGetPersonByLastName_success() throws JsonProcessingException {
    PersonProfile p = new PersonProfile();
    p.setLastName("Bond");

    stubFor(
        get(urlPathEqualTo("/person-profile-equal-last-name/"))
            .withQueryParam("lastName", equalTo("Bond"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[" + mapper.writeValueAsString(p) + "]")));

    List<PersonProfile> profiles = api.getPersonByLastName("Bond");

    assertThat(profiles).hasSize(1);
    assertThat(profiles.get(0).getLastName()).isEqualTo("Bond");
  }

  // ============================
  // DELETE PERSON
  // ============================

  @Test
  void testDeletePersonProfile_success() {

    stubFor(delete(urlEqualTo("/person-profile/P777")).willReturn(aResponse().withStatus(204)));

    api.deletePersonProfile("P777");

    verify(signatureMock).signDeleteRequest("P777");
  }
}
