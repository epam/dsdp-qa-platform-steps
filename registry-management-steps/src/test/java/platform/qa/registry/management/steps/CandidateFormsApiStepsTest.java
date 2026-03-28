package platform.qa.registry.management.steps;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.registry.management.dto.request.form.CreateFormRequest;
import platform.qa.registry.management.dto.request.form.Form;
import platform.qa.registry.management.dto.response.EntityInfo;
import platform.qa.registry.management.dto.response.ErrorBody;
import platform.qa.registry.management.dto.response.ErrorResponse;

class CandidateFormsApiStepsTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static WireMockServer wireMock;

  private CandidateFormsApiSteps steps;

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
  void init() {
    Service service =
        new Service("http://localhost:" + wireMock.port(), new User("usr", "token123"));
    steps = new CandidateFormsApiSteps(service);
  }

  // ------------------------------------------------------------
  // GET FORM AS FORM (200)
  // ------------------------------------------------------------
  @Test
  void testGetFormContentAsForm_success() throws Exception {
    Form form = new Form();
    form.setName("myForm");
    form.setTitle("Test");
    form.setType("form");

    stubFor(
        get(urlEqualTo("/versions/candidates/v1/forms/myForm"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(form))));

    Form response = steps.getFormContentAsFormByFormNameForVersionCandidate("myForm", "v1");

    assertThat(response.getName()).isEqualTo("myForm");
    assertThat(response.getTitle()).isEqualTo("Test");
  }

  // ------------------------------------------------------------
  // GET FORM AS STRING (200)
  // ------------------------------------------------------------
  @Test
  void testGetFormContentAsString_success() {
    String body = "{\"field\": \"value\"}";

    stubFor(
        get(urlEqualTo("/versions/candidates/v1/forms/myForm"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(body)));

    String response = steps.getFormContentByFormNameForVersionCandidate("myForm", "v1");
    assertThat(response).contains("value");
  }

  // ------------------------------------------------------------
  // GET FORM LIST (200)
  // ------------------------------------------------------------
  @Test
  void testGetFormList_success() throws Exception {
    EntityInfo info = new EntityInfo();
    info.setName("myForm");

    List<EntityInfo> list = List.of(info);

    stubFor(
        get(urlEqualTo("/versions/candidates/v1/forms"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(list))));

    List<EntityInfo> response = steps.getFormListFromVersionCandidate("v1");

    assertThat(response).hasSize(1);
    assertThat(response.get(0).getName()).isEqualTo("myForm");
  }

  // ------------------------------------------------------------
  // GET FORM UNAUTHORIZED (401)
  // ------------------------------------------------------------
  @Test
  void testGetFormUnauthorized() throws Exception {
    ErrorResponse err =
        new ErrorResponse(new ErrorBody("123", "unauthorized", null, "ERR", "details"));

    stubFor(
        get(urlEqualTo("/versions/candidates/v1/forms/myForm"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_UNAUTHORIZED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(err))));

    ErrorResponse resp = steps.getFormContentByFormNameForVersionUnauthorized("myForm", "v1");

    assertThat(resp.getError().getMessage()).isEqualTo("unauthorized");
  }

  // ------------------------------------------------------------
  // CREATE FORM (Form) — 201 CREATED
  // ------------------------------------------------------------
  @Test
  void testCreateFormForm_success() throws Exception {
    Form req = new Form();
    req.setName("form1");
    req.setTitle("Form Title");

    stubFor(
        post(urlEqualTo("/versions/candidates/v1/forms/form1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CREATED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(req))));

    Form resp = steps.createFormForVersionCandidateById(req, "v1", "form1");

    assertThat(resp.getName()).isEqualTo("form1");
    assertThat(resp.getTitle()).isEqualTo("Form Title");
  }

  // ------------------------------------------------------------
  // CREATE FORM (CreateFormRequest) — 201
  // ------------------------------------------------------------
  @Test
  void testCreateFormWithCreateFormRequest_success() throws Exception {
    CreateFormRequest req = new CreateFormRequest("formX", "TitleX", "form");

    stubFor(
        post(urlEqualTo("/versions/candidates/v1/forms/formX"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CREATED)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(req))));

    CreateFormRequest resp = steps.createFormForVersionCandidateById(req, "v1", "formX");

    assertThat(resp.getName()).isEqualTo("formX");
  }

  // ------------------------------------------------------------
  // CREATE FORM returning string — 201
  // ------------------------------------------------------------
  @Test
  void testCreateFormString_success() {
    String id = "v1";
    CreateFormRequest req = new CreateFormRequest("f1", "Title", "form");

    stubFor(
        post(urlEqualTo("/versions/candidates/v1/forms/f1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_CREATED)
                    .withHeader("Content-Type", "application/json")
                    .withBody("\"CREATED\"")));

    String resp = steps.createForm(id, req);
    assertThat(resp).contains("CREATED");
  }

  // ------------------------------------------------------------
  // UPDATE FORM — 200 OK
  // ------------------------------------------------------------
  @Test
  void testUpdateForm_success() throws Exception {
    Form req = new Form();
    req.setName("form1");
    req.setTitle("Updated");

    stubFor(
        put(urlEqualTo("/versions/candidates/v1/forms/form1"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(req))));

    Form resp = steps.updateFormContentByFormNameForVersionCandidate(req, "form1", "v1", Map.of());

    assertThat(resp.getTitle()).isEqualTo("Updated");
  }

  // ------------------------------------------------------------
  // UPDATE FORM — CreateFormRequest variant — 200 OK
  // ------------------------------------------------------------
  @Test
  void testUpdateFormCreateFormRequest_success() throws Exception {
    CreateFormRequest req = new CreateFormRequest("f2", "T", "form");

    stubFor(
        put(urlEqualTo("/versions/candidates/v1/forms/f2"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.SC_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mapper.writeValueAsString(req))));

    CreateFormRequest resp = steps.updateFormContentByFormNameForVersionCandidate(req, "f2", "v1");

    assertThat(resp.getName()).isEqualTo("f2");
  }

  // ------------------------------------------------------------
  // DELETE FORM — 204 NO CONTENT
  // ------------------------------------------------------------
  @Test
  void testDeleteForm_success() {
    stubFor(
        delete(urlEqualTo("/versions/candidates/v1/forms/myForm"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

    steps.deleteFormFromVersionCandidate("myForm", "v1");
  }

  // ------------------------------------------------------------
  // DELETE FORM with headers — 204
  // ------------------------------------------------------------
  @Test
  void testDeleteFormWithHeaders_success() {
    stubFor(
        delete(urlEqualTo("/versions/candidates/v1/forms/myForm"))
            .willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

    steps.deleteFormFromVersionCandidate("myForm", "v1", Map.of("h", "v"));
  }
}
