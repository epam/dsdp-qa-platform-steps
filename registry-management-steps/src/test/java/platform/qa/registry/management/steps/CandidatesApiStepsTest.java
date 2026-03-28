package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.request.CreateVersionCandidateRequest;
import platform.qa.registry.management.dto.response.*;
import platform.qa.registry.management.dto.response.unsupported.UnsupportedCreateCandidateRequest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CandidatesApiStepsTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private WireMockServer wireMock;
  private CandidatesApiSteps steps;

  @BeforeAll
  void init() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    Service service =
        new Service("http://localhost:" + wireMock.port(), new User("usr", "token123"));
    steps = new CandidatesApiSteps(service);
  }

  @AfterAll
  void tearDown() {
    wireMock.stop();
  }

  // =====================================================================
  //                          HELPERS
  // =====================================================================

  private String json(Object obj) throws Exception {
    return mapper.writeValueAsString(obj);
  }

  private ErrorResponse err(String msg) {
    return ErrorResponse.builder()
        .error(
            ErrorBody.builder().message(msg).code("ERR").traceId("123").details("details").build())
        .build();
  }

  private ErrorBody errBody(String msg) {
    return ErrorBody.builder().message(msg).code("ERR").traceId("123").details("details").build();
  }

  // =====================================================================
  //                    GET CANDIDATES LIST
  // =====================================================================

  @Test
  void testGetCandidatesList() throws Exception {
    List<VersionInfo> list = List.of(new VersionInfo("desc", "1", "v1"));

    stubFor(
        get(urlEqualTo("/versions/candidates"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(list))));

    List<VersionInfo> resp = steps.getCandidatesList();

    assertThat(resp).hasSize(1);
    assertThat(resp.get(0).getName()).isEqualTo("v1");
  }

  @Test
  void testGetCandidatesListUnauthorized() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/candidates"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(err("unauthorized list")))));

    ErrorResponse resp = steps.getCandidatesListUnauthorized();

    assertThat(resp.getError().getMessage()).isEqualTo("unauthorized list");
  }

  // =====================================================================
  //              CREATE VERSION CANDIDATE (201 + ERRORS)
  // =====================================================================

  @Test
  void testCreateVersionCandidate() throws Exception {
    CreateVersionCandidateRequest req = new CreateVersionCandidateRequest("v-test", "desc");

    CreatedVersionResponse out =
        new CreatedVersionResponse(
            "1", "v-test", "desc", "author", "date", "upd", false, List.of(), List.of());

    stubFor(
        post(urlEqualTo("/versions/candidates"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CREATED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(out))));

    CreatedVersionResponse resp = steps.createVersionCandidate(req);

    assertThat(resp.getId()).isEqualTo("1");
    assertThat(resp.getName()).isEqualTo("v-test");
  }

  @Test
  void testCreateVersionCandidateUnsupported() throws Exception {
    UnsupportedCreateCandidateRequest req = new UnsupportedCreateCandidateRequest("bad");

    stubFor(
        post(urlEqualTo("/versions/candidates"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(err("unsupported payload")))));

    ErrorResponse resp = steps.createVersionCandidateUnsupported(req);

    assertThat(resp.getError().getMessage()).isEqualTo("unsupported payload");
  }

  @Test
  void testCreateVersionCandidateUnauthorized() throws Exception {
    CreateVersionCandidateRequest req = new CreateVersionCandidateRequest("v-test", "desc");

    stubFor(
        post(urlEqualTo("/versions/candidates"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(err("unauthorized create")))));

    ErrorResponse resp = steps.createVersionCandidateUnauthorized(req);

    assertThat(resp.getError().getMessage()).isEqualTo("unauthorized create");
  }

  // =====================================================================
  //            GET VERSION CANDIDATE INFO / REBASE INFO
  // =====================================================================

  @Test
  void testGetVersionCandidateInfo() throws Exception {
    VersionCandidateInfoResponse info =
        VersionCandidateInfoResponse.builder()
            .id("55")
            .name("test")
            .description("desc")
            .hasConflicts(false)
            .published(true)
            .build();

    stubFor(
        get(urlEqualTo("/versions/candidates/55"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(info))));

    VersionCandidateInfoResponse resp = steps.getVersionCandidateInfo("55");

    assertThat(resp.getId()).isEqualTo("55");
    assertThat(resp.getName()).isEqualTo("test");
  }

  @Test
  void testGetVersionCandidateRebaseInfo() throws Exception {
    VersionCandidateInfoRebaseResponse info = new VersionCandidateInfoRebaseResponse();
    info.setId("88");
    info.setLatestUpdate("now");

    stubFor(
        get(urlEqualTo("/versions/candidates/88"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(info))));

    VersionCandidateInfoRebaseResponse resp = steps.getVersionCandidateRebaseInfo("88");

    assertThat(resp.getId()).isEqualTo("88");
    assertThat(resp.getLatestUpdate()).isEqualTo("now");
  }

  @Test
  void testGetVersionCandidateInfoNotFound() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/candidates/99"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_NOT_FOUND)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(err("not found")))));

    ErrorResponse resp = steps.getVersionCandidateInfoNotFound("99");

    assertThat(resp.getError().getMessage()).isEqualTo("not found");
  }

  @Test
  void testGetVersionCandidateInfoUnauthorized() throws Exception {
    stubFor(
        get(urlEqualTo("/versions/candidates/77"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(err("unauth")))));

    ErrorResponse resp = steps.getVersionCandidateInfoUnauthorized("77");

    assertThat(resp.getError().getMessage()).isEqualTo("unauth");
  }

  // =====================================================================
  //                      SUBMIT & REBASE
  // =====================================================================

  @Test
  void testSubmitChangeForVersionCandidate() {
    stubFor(
        post(urlEqualTo("/versions/candidates/55/submit"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_NO_CONTENT) // 204
                    .withHeader("Content-Type", "application/json")));

    steps.submitChangeForVersionCandidate("55");

    wireMock.verify(postRequestedFor(urlEqualTo("/versions/candidates/55/submit")));
  }

  @Test
  void testRebaseVersionCandidate() {
    stubFor(
        put(urlEqualTo("/versions/candidates/55/rebase"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")));

    steps.rebaseVersionCandidate("55");

    wireMock.verify(putRequestedFor(urlEqualTo("/versions/candidates/55/rebase")));
  }

  @Test
  void testSubmitChangeForVersionCandidateMergeConflict() throws Exception {
    stubFor(
        post(urlEqualTo("/versions/candidates/55/submit"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CONFLICT)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(errBody("conflict")))));

    ErrorBody resp = steps.submitChangeForVersionCandidateMergeConflict("55");

    assertThat(resp.getMessage()).isEqualTo("conflict");
  }

  // =====================================================================
  //                           GET CHANGES
  // =====================================================================

  @Test
  void testGetChanges() throws Exception {
    ChangeInfoResponse out =
        ChangeInfoResponse.builder()
            .changedForms(List.of(new Change("f1", "Form1", "MODIFIED", false)))
            .changedBusinessProcesses(List.of())
            .changedDataModelFiles(List.of())
            .changedGroups(List.of())
            .changedI18nFiles(List.of())
            .build();

    stubFor(
        get(urlEqualTo("/versions/candidates/33/changes"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json(out))));

    ChangeInfoResponse resp = steps.getChanges("33");

    assertThat(resp.getChangedForms()).hasSize(1);
    assertThat(resp.getChangedForms().get(0).getName()).isEqualTo("f1");
  }
}
