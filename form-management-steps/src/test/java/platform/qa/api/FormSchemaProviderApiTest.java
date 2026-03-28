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

class FormSchemaProviderApiTest {

  private WireMockServer wireMock;
  private FormSchemaProviderApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("usr", "pwd");
    user.setToken("token123");

    Service service = new Service("http://localhost:" + wireMock.port(), user);

    api = new FormSchemaProviderApi(service);
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  // ---------------------------------------------------------
  // GET FORM BY NAME
  // ---------------------------------------------------------
  @Test
  void testGetFormByName_success() {
    stubFor(
        get(urlEqualTo("/api/forms/TestForm"))
            .willReturn(
                okJson(
                    """
                        {"name":"TestForm","version":1}
                        """)));

    var result = api.getFormByName("TestForm");

    assertThat(result).containsEntry("name", "TestForm").containsEntry("version", 1);
  }

  // ---------------------------------------------------------
  // GET FORM BY NAME – RAW response (no status assertion)
  // ---------------------------------------------------------
  @Test
  void testGetSearchFormByName_success() {
    stubFor(get(urlEqualTo("/api/forms/AnyForm")).willReturn(okJson("{\"name\":\"AnyForm\"}")));

    Response resp = api.getSearchFormByName("AnyForm");

    assertThat(resp.statusCode()).isEqualTo(200);
    assertThat(resp.jsonPath().getString("name")).isEqualTo("AnyForm");
  }

  // ---------------------------------------------------------
  // DELETE FORM
  // ---------------------------------------------------------
  @Test
  void testDeleteForm_success() {
    stubFor(delete(urlEqualTo("/api/forms/F123")).willReturn(noContent()));

    api.deleteForm("F123");

    verify(deleteRequestedFor(urlEqualTo("/api/forms/F123")));
  }

  @Test
  void testSendDeleteForm_success() {
    stubFor(delete(urlEqualTo("/api/forms/F999")).willReturn(noContent()));

    Response resp = api.sendDeleteForm("F999");

    assertThat(resp.statusCode()).isEqualTo(204);
  }

  @Test
  void testSendDeleteFormWithoutAuth_403() {
    stubFor(delete(urlEqualTo("/api/forms/FORBIDDEN")).willReturn(forbidden()));

    Response resp = api.sendDeleteFormWithoutAuth("FORBIDDEN");

    assertThat(resp.statusCode()).isEqualTo(403);
  }

  @Test
  void testSendDeleteFormWithCustomToken_success() {
    stubFor(
        delete(urlEqualTo("/api/forms/F100"))
            .withHeader("X-Access-Token", equalTo("SUPER_TOKEN"))
            .willReturn(noContent()));

    Response resp = api.sendDeleteFormWithCustomToken("F100", "SUPER_TOKEN");

    assertThat(resp.statusCode()).isEqualTo(204);

    verify(
        deleteRequestedFor(urlEqualTo("/api/forms/F100"))
            .withHeader("X-Access-Token", equalTo("SUPER_TOKEN")));
  }

  // ---------------------------------------------------------
  // CREATE FORM (payload)
  // ---------------------------------------------------------
  @Test
  void testCreateForm_success() {
    stubFor(post(urlEqualTo("/api/forms")).willReturn(created()));

    api.createForm(Map.of("field", "value"));

    verify(postRequestedFor(urlEqualTo("/api/forms")));
  }

  // ---------------------------------------------------------
  // CREATE FORM (file lines)
  // ---------------------------------------------------------
  @Test
  void testCreateForm_fromLines_success() {
    stubFor(post(urlEqualTo("/api/forms")).willReturn(created()));

    api.createForm(List.of("line1", "line2"));

    verify(postRequestedFor(urlEqualTo("/api/forms")));
  }

  // ---------------------------------------------------------
  // sendPostForm → response returned
  // ---------------------------------------------------------
  @Test
  void testSendPostForm_success() {
    stubFor(post(urlEqualTo("/api/forms")).willReturn(created().withBody("{\"ok\":true}")));

    Response resp = api.sendPostForm(Map.of("k", "v"));

    assertThat(resp.statusCode()).isEqualTo(201);
    assertThat(resp.jsonPath().getBoolean("ok")).isTrue();
  }

  // ---------------------------------------------------------
  // PUT form
  // ---------------------------------------------------------
  @Test
  void testSendPutForm_success() {
    stubFor(put(urlEqualTo("/api/forms/F1")).willReturn(ok()));

    Response resp = api.sendPutForm(Map.of("n", 1), "F1");

    assertThat(resp.statusCode()).isEqualTo(200);
  }

  @Test
  void testUpdateForm_success() {
    stubFor(put(urlEqualTo("/api/forms/F9")).willReturn(ok()));

    Response resp = api.updateForm(Map.of("x", 2), "F9");

    assertThat(resp.statusCode()).isEqualTo(200);
  }

  // ---------------------------------------------------------
  // NEGATIVE AUTH TESTS
  // ---------------------------------------------------------
  @Test
  void testSendPostFormWithoutAuth_403() {
    stubFor(post(urlEqualTo("/api/forms")).willReturn(forbidden()));

    Response resp = api.sendPostFormWithoutAuth(Map.of("a", 1));

    assertThat(resp.statusCode()).isEqualTo(403);
  }

  @Test
  void testSendPostFormWithCustomToken_401() {
    stubFor(post(urlEqualTo("/api/forms")).willReturn(unauthorized()));

    Response resp = api.sendPostFormWithCustomToken(Map.of("a", 1), "BAD");

    assertThat(resp.statusCode()).isEqualTo(401);
  }

  @Test
  void testSendGetFormWithoutAuth_401() {
    stubFor(get(urlEqualTo("/api/forms/AAA")).willReturn(unauthorized()));

    Response resp = api.sendGetFormWithoutAuth("AAA");

    assertThat(resp.statusCode()).isEqualTo(401);
  }

  @Test
  void testSendPutFormWithoutAuth_401() {
    stubFor(put(urlEqualTo("/api/forms/X")).willReturn(unauthorized()));

    Response resp = api.sendPutFormWithoutAuth(Map.of("p", 9), "X");

    assertThat(resp.statusCode()).isEqualTo(401);
  }

  // ---------------------------------------------------------
  // RAW BODY tests
  // ---------------------------------------------------------
  @Test
  void testSendPostFormRawBody_invalidJson() {
    stubFor(post(urlEqualTo("/api/forms")).willReturn(badRequest()));

    Response resp = api.sendPostFormRawBody("INVALID RAW BODY");

    assertThat(resp.statusCode()).isEqualTo(400);
  }

  @Test
  void testSendPutFormRawBody_invalidJson() {
    stubFor(put(urlEqualTo("/api/forms/FormX")).willReturn(badRequest()));

    Response resp = api.sendPutFormRawBody("WRONG", "FormX");

    assertThat(resp.statusCode()).isEqualTo(400);
  }

  @Test
  void testSendGetFormWithCustomToken_success() {
    stubFor(
        get(urlEqualTo("/api/forms/BBB"))
            .withHeader("X-Access-Token", equalTo("CUSTOM"))
            .willReturn(okJson("{\"name\":\"BBB\"}")));

    Response resp = api.sendGetFormWithCustomToken("BBB", "CUSTOM");

    assertThat(resp.statusCode()).isEqualTo(200);
    assertThat(resp.jsonPath().getString("name")).isEqualTo("BBB");

    verify(
        getRequestedFor(urlEqualTo("/api/forms/BBB"))
            .withHeader("X-Access-Token", equalTo("CUSTOM")));
  }

  @Test
  void testSendPutFormWithCustomToken_success() {
    stubFor(
        put(urlEqualTo("/api/forms/F22"))
            .withHeader("X-Access-Token", equalTo("SUPER"))
            .willReturn(ok()));

    Response resp = api.sendPutFormWithCustomToken(Map.of("key", "val"), "F22", "SUPER");

    assertThat(resp.statusCode()).isEqualTo(200);

    verify(
        putRequestedFor(urlEqualTo("/api/forms/F22"))
            .withHeader("X-Access-Token", equalTo("SUPER")));
  }
}
