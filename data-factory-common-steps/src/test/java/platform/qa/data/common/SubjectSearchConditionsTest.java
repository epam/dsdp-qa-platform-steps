package platform.qa.data.common;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.Subject;
import platform.qa.entities.User;
import platform.qa.pojo.common.SubjectSettings;

class SubjectSearchConditionsTest {

  private WireMockServer wireMock;
  private Service dataFactory;
  private SubjectSearchConditions api;

  @BeforeEach
  void setup() {

    wireMock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("usr", "pwd");
    user.setToken("token");

    dataFactory = new Service("http://localhost:" + wireMock.port(), user);
    api = new SubjectSearchConditions(dataFactory);
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  // -------------------------------------------------------------------------
  // TEST subjectEqual()
  // -------------------------------------------------------------------------

  @Test
  void testSubjectEqual_success() {
    wireMock.stubFor(
        get(urlPathEqualTo("/subject-equal-subject-type-equal-subject-code/"))
            .withQueryParams(
                Map.of(
                    "subjectType", equalTo("PHYS"),
                    "subjectCode", equalTo("12345")))
            .willReturn(okJson("[ { \"subjectId\": \"S1\", \"subjectName\": \"Test User\" } ]")));

    Subject result = api.subjectEqual("PHYS", "12345");

    assertThat(result).isNotNull();
    assertThat(result.getSubjectId()).isEqualTo("S1");
    assertThat(result.getSubjectName()).isEqualTo("Test User");
  }

  @Test
  void testSubjectEqual_nullParams_returnsNull() {
    assertThat(api.subjectEqual(null, "123")).isNull();
    assertThat(api.subjectEqual("PHYS", null)).isNull();
    assertThat(api.subjectEqual(null, null)).isNull();
  }

  @Test
  void testSubjectEqual_emptyResponse_returnsNullObject() {
    wireMock.stubFor(
        get(urlPathEqualTo("/subject-equal-subject-type-equal-subject-code/"))
            .withQueryParam("subjectType", equalTo("LEGAL"))
            .withQueryParam("subjectCode", equalTo("999"))
            .willReturn(okJson("[]")));

    Subject result = api.subjectEqual("LEGAL", "999");

    // JSONPath "[0]" === null
    assertThat(result).isNull();
  }

  // -------------------------------------------------------------------------
  // TEST subjectSettings()
  // -------------------------------------------------------------------------

  @Test
  void testSubjectSettings_success() {
    wireMock.stubFor(
        get(urlPathEqualTo("/subject-settings-equal-settings-id/"))
            .withQueryParam("settingsId", equalTo("SET1"))
            .willReturn(
                okJson(
                    "[ { \"subjectSettingsId\": \"SET1\", \"subjectId\": \"U1\", \"settingsId\": \"CFG1\" } ]")));

    List<SubjectSettings> result = api.subjectSettings("SET1");

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);

    SubjectSettings s = result.get(0);

    assertThat(s.getSubjectSettingsId()).isEqualTo("SET1");
    assertThat(s.getSubjectId()).isEqualTo("U1");
    assertThat(s.getSettingsId()).isEqualTo("CFG1");
  }

  @Test
  void testSubjectSettings_null_returnsNull() {
    assertThat(api.subjectSettings(null)).isNull();
  }

  @Test
  void testSubjectSettings_emptyList() {

    wireMock.stubFor(
        get(urlPathEqualTo("/subject-settings-equal-settings-id/"))
            .withQueryParam("settingsId", equalTo("EMPTY"))
            .willReturn(okJson("[]")));

    List<SubjectSettings> result = api.subjectSettings("EMPTY");

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }
}
