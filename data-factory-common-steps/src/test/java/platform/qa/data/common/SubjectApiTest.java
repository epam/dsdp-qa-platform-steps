package platform.qa.data.common;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import platform.qa.entities.Service;
import platform.qa.entities.Subject;
import platform.qa.entities.User;
import platform.qa.pojo.common.SubjectProfile;
import platform.qa.pojo.common.SubjectSettings;

public class SubjectApiTest {

  private static WireMockServer wireMock;
  private Service dataFactory;
  private SubjectApi api;
  private SignatureSteps signatureMock;

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
  void setup() throws Exception {

    try (MockedConstruction<SignatureSteps> mocked =
        Mockito.mockConstruction(
            SignatureSteps.class,
            (mock, ctx) -> {
              Mockito.when(mock.signRequest(any())).thenReturn("SIG-ID");
              Mockito.when(mock.signDeleteRequest(any())).thenReturn("DEL-ID");
            })) {

      User user = new User("usr", "pwd");
      user.setToken("token");
      dataFactory = new Service("http://localhost:" + wireMock.port(), user);

      api = new SubjectApi(dataFactory, dataFactory, List.of());

      signatureMock = mocked.constructed().get(0);

      Field f = SubjectApi.class.getDeclaredField("signatureSteps");
      f.setAccessible(true);
      f.set(api, signatureMock);
    }
  }

  // ----------------------------------------------------------------------
  // CREATE subject (status 201)
  // ----------------------------------------------------------------------

  @Test
  void testCreateSubject_success() {
    stubFor(
        post(urlEqualTo("/subject/"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"SUB123\"}")));

    Subject s = new Subject();
    s.setSubjectName("test");

    String id = api.createSubject(s);

    assertThat(id).isEqualTo("SUB123");
    assertThat(s.getSubjectId()).isEqualTo("SUB123");
    Mockito.verify(signatureMock).signRequest(s);
  }

  // ----------------------------------------------------------------------
  // tryToCreateSubject (negative creation)
  // ----------------------------------------------------------------------

  @Test
  void testTryToCreateSubject_conflict() {
    stubFor(post(urlEqualTo("/subject/")).willReturn(aResponse().withStatus(409)));

    Subject s = new Subject();
    var resp = api.tryToCreateSubject(s);

    assertThat(resp.statusCode()).isEqualTo(409);
    Mockito.verify(signatureMock).signRequest(s);
  }

  // ----------------------------------------------------------------------
  // getUserSubject
  // ----------------------------------------------------------------------

  @Test
  void testGetUserSubject_success() {
    stubFor(
        get(urlEqualTo("/subject/SUB42"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"subjectId\":\"SUB42\",\"subjectName\":\"Alisa\"}")));

    Subject s = api.getUserSubject("SUB42");

    assertThat(s.getSubjectId()).isEqualTo("SUB42");
    assertThat(s.getSubjectName()).isEqualTo("Alisa");
  }

  // ----------------------------------------------------------------------
  // getUserSubjectSettingsProfile
  // ----------------------------------------------------------------------

  @Test
  void testGetUserSubjectSettingsProfile_success() {
    stubFor(
        get(urlEqualTo("/subject-settings/PR123"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"subjectId\":\"PR123\"}")));

    SubjectProfile p = api.getUserSubjectSettingsProfile("PR123");

    assertThat(p.getSubjectId()).isEqualTo("PR123");
  }

  // ----------------------------------------------------------------------
  // getSubjectSettings
  // ----------------------------------------------------------------------

  @Test
  void testGetSubjectSettings_success() {
    stubFor(
        get(urlEqualTo("/subject-settings/ST55"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"subjectSettingsId\":\"ST55\"}")));

    SubjectSettings st = api.getSubjectSettings("ST55");

    assertThat(st.getSubjectSettingsId()).isEqualTo("ST55");
  }

  // ----------------------------------------------------------------------
  // updateSubjectSettings
  // ----------------------------------------------------------------------

  @Test
  void testUpdateSubjectSettings_success() {
    SubjectSettings st = new SubjectSettings();
    st.setSubjectSettingsId("ST111");

    stubFor(put(urlEqualTo("/subject-settings/ST111")).willReturn(aResponse().withStatus(204)));

    api.updateSubjectSettings(st);

    Mockito.verify(signatureMock).signRequest(st);
  }

  // ----------------------------------------------------------------------
  // deleteSubjectSettings
  // ----------------------------------------------------------------------

  @Test
  void testDeleteSubjectSettings_success() {
    SubjectSettings st = new SubjectSettings();
    st.setSubjectSettingsId("ST888");

    stubFor(delete(urlEqualTo("/subject-settings/ST888")).willReturn(aResponse().withStatus(204)));

    api.deleteSubjectSettings(st);

    Mockito.verify(signatureMock).signDeleteRequest("ST888");
  }

  // ----------------------------------------------------------------------
  // deleteUserSubjectSettingsProfile
  // ----------------------------------------------------------------------

  @Test
  void testDeleteUserSubjectSettingsProfile_success() {
    stubFor(delete(urlEqualTo("/subject-settings/PR77")).willReturn(aResponse().withStatus(204)));

    api.deleteUserSubjectSettingsProfile("PR77");

    Mockito.verify(signatureMock).signDeleteRequest("PR77");
  }

  // ----------------------------------------------------------------------
  // deleteUserSubject
  // ----------------------------------------------------------------------

  @Test
  void testDeleteUserSubject_success() {
    stubFor(delete(urlEqualTo("/subject/SUB999")).willReturn(aResponse().withStatus(204)));

    api.deleteUserSubject("SUB999");

    Mockito.verify(signatureMock).signDeleteRequest("SUB999");
  }

  // ----------------------------------------------------------------------
  // updateUserSubject
  // ----------------------------------------------------------------------

  @Test
  void testUpdateUserSubject_success() {
    Subject s = new Subject();
    s.setSubjectId("SUB15");

    stubFor(put(urlEqualTo("/subject/SUB15")).willReturn(aResponse().withStatus(204)));

    api.updateUserSubject(s);

    Mockito.verify(signatureMock).signRequest(s);
  }

  // ----------------------------------------------------------------------
  // createSubjectIfNotExisted: new subject (201)
  // ----------------------------------------------------------------------

  @Test
  void testCreateSubjectIfNotExisted_new() {
    stubFor(
        post(urlEqualTo("/subject/"))
            .willReturn(aResponse().withStatus(201).withBody("{\"id\":\"NEWID\"}")));

    Subject s = new Subject();
    s.setSubjectName("User X");

    Subject out = api.createSubjectIfNotExisted(s);

    assertThat(out.getSubjectId()).isEqualTo("NEWID");
  }

  // ----------------------------------------------------------------------
  // createSubjectIfNotExisted: already exists (409)
  // ----------------------------------------------------------------------

  @Test
  void testCreateSubjectIfNotExisted_conflict() {
    stubFor(post(urlEqualTo("/subject/")).willReturn(aResponse().withStatus(409)));

    stubFor(
        get(urlMatching("/subject-search/.*"))
            .willReturn(aResponse().withStatus(200).withBody("{\"subjectId\":\"EXIST123\"}")));

    Subject s = new Subject();
    s.setSubjectType("PERSON");
    s.setSubjectCode("12345");

    try (MockedConstruction<SubjectSearchConditions> mockedSearch =
        Mockito.mockConstruction(
            SubjectSearchConditions.class,
            (mock, ctx) -> {
              Mockito.when(mock.subjectEqual(any(), any()))
                  .thenReturn(
                      new Subject() {
                        {
                          setSubjectId("EXIST123");
                        }
                      });
            })) {

      Subject out = api.createSubjectIfNotExisted(s);

      assertThat(out.getSubjectId()).isEqualTo("EXIST123");
    }
  }
}
