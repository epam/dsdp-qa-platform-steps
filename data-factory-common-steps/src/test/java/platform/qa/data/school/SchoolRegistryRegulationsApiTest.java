package platform.qa.data.school;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.school.EducationalType;

class SchoolRegistryRegulationsApiTest {

  private WireMockServer wireMock;
  private SchoolRegistryRegulationsApi api;

  private SignatureSteps signatureMock;
  private MockedConstruction<SignatureSteps> mockSignatureStepsCtor;

  @BeforeEach
  void setup() throws NoSuchFieldException, IllegalAccessException {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    mockSignatureStepsCtor =
        Mockito.mockConstruction(
            SignatureSteps.class,
            (mock, ctx) -> {
              when(mock.signRequest(any())).thenReturn("SIGN123");
              when(mock.signDeleteRequest(any())).thenReturn("DEL123");
            });

    User user = new User("usr", "pwd");
    user.setToken("token");

    Service service = new Service("http://localhost:" + wireMock.port(), user);

    api = new SchoolRegistryRegulationsApi(service, service, List.of(new Redis("mock", "pwd")));

    // Replace real SignatureSteps inside API
    signatureMock = mockSignatureStepsCtor.constructed().get(0);
    var f = SchoolRegistryRegulationsApi.class.getDeclaredField("signatureSteps");
    f.setAccessible(true);
    f.set(api, signatureMock);
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
    mockSignatureStepsCtor.close();
  }

  // ----------------------------------------------------------
  // GET ALL TYPES
  // ----------------------------------------------------------
  @Test
  void testGetAllEducationalType_success() {
    stubFor(
        get(urlPathMatching("/edu-type-contains"))
            .willReturn(
                okJson(
                    """
                        [
                          {"eduTypeId":"1","name":"School"},
                          {"eduTypeId":"2","name":"University"}
                        ]
                        """)));

    List<EducationalType> list = api.getAllEducationalType();

    assertThat(list).hasSize(2);
    assertThat(list.get(0).getName()).isEqualTo("School");
  }

  // ----------------------------------------------------------
  // GET BY NAME
  // ----------------------------------------------------------
  @Test
  void testGetAllEducationalTypeByName_success() {
    stubFor(
        get(urlPathMatching("/edu-type-contains.*"))
            .willReturn(
                okJson(
                    """
                        [
                          {"eduTypeId":"3","name":"Gymnasium"}
                        ]
                        """)));

    List<EducationalType> result = api.getAllEducationalTypeByName("Gymnasium");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getEduTypeId()).isEqualTo("3");
  }

  // ----------------------------------------------------------
  // CREATE
  // ----------------------------------------------------------
  @Test
  void testCreateEducationalType_success() {
    stubFor(post(urlEqualTo("/edu-type/")).willReturn(created().withBody("{\"id\":\"NEW123\"}")));

    EducationalType input = EducationalType.builder().name("College").build();

    EducationalType created = api.createEducationalType(input);

    verify(signatureMock).signRequest(any());
    assertThat(created.getEduTypeId()).isEqualTo("NEW123");
    assertThat(created.getName()).isEqualTo("College");
  }

  // ----------------------------------------------------------
  // DELETE BY ID
  // ----------------------------------------------------------
  @Test
  void testDeleteEducationalTypeById_success() {
    stubFor(delete(urlEqualTo("/edu-type/DELME")).willReturn(noContent()));

    api.deleteEducationalTypeById("DELME");

    verify(signatureMock).signDeleteRequest("DELME");
    verify(deleteRequestedFor(urlEqualTo("/edu-type/DELME")));
  }

  // ----------------------------------------------------------
  // DELETE ALL BY NAME
  // ----------------------------------------------------------
  @Test
  void testDeleteAllEducationalTypeByName_success() {
    // 1) GET returns 2 entries
    stubFor(
        get(urlPathMatching("/edu-type-contains.*"))
            .willReturn(
                okJson(
                    """
                        [
                          {"eduTypeId":"A1","name":"Test"},
                          {"eduTypeId":"A2","name":"Test"},
                          {"eduTypeId":"B1","name":"Other"}
                        ]
                        """)));

    // 2) DELETE endpoints
    stubFor(delete(urlEqualTo("/edu-type/A1")).willReturn(noContent()));
    stubFor(delete(urlEqualTo("/edu-type/A2")).willReturn(noContent()));

    api.deleteAllEducationalTypeByName("Test");

    verify(deleteRequestedFor(urlEqualTo("/edu-type/A1")));
    verify(deleteRequestedFor(urlEqualTo("/edu-type/A2")));
  }
}
