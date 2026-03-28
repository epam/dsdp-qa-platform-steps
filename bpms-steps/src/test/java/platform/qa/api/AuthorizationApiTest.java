package platform.qa.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.entities.auth.AuthorizationRequest;

class AuthorizationApiTest {

  private WireMockServer wireMock;
  private AuthorizationApi api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("user", "pwd");
    user.setToken("TEST_TOKEN");
    Service service = new Service("http://localhost:" + wireMock.port(), user);

    api = new AuthorizationApi(service);

    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterEach
  void tearDown() {
    wireMock.stop();
  }

  @Test
  void testCreateAuthorization_success() {
    stubFor(
        post(urlEqualTo("/api/authorization/create"))
            .willReturn(
                okJson(
                    """
                                        {"id":"AUTH1"}
                                        """)));

    AuthorizationRequest request =
        AuthorizationRequest.builder()
            .resourceType(1)
            .resourceId("PROC_KEY")
            .permissions(List.of("1"))
            .build();

    String id = api.createAuthorization(request);

    assertThat(id).isEqualTo("AUTH1");

    verify(postRequestedFor(urlEqualTo("/api/authorization/create")));
  }

  @Test
  void testGetAuthorizations_success() {
    stubFor(
        get(urlEqualTo("/api/authorization"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"A1"},
                                          {"id":"A2"}
                                        ]
                                        """)));

    List<Map> list = api.getAuthorizations();

    assertThat(list).hasSize(2);
    assertThat(list.get(0)).containsEntry("id", "A1");
  }

  @Test
  void testGetAuthorizationsByParam_success() {
    stubFor(
        get(urlPathEqualTo("/api/authorization"))
            .withQueryParam("userIdIn", equalTo("user1"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"A3"}
                                        ]
                                        """)));

    List<Map> list = api.getAuthorizationsByParam("userIdIn", "user1");

    assertThat(list).hasSize(1);
    assertThat(list.get(0)).containsEntry("id", "A3");
  }

  @Test
  void testGetAuthorizationsByParams_success() {
    stubFor(
        get(urlPathEqualTo("/api/authorization"))
            .withQueryParam("groupIdIn", equalTo("group1"))
            .withQueryParam("resourceId", equalTo("PROC"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"A4"}
                                        ]
                                        """)));

    List<Map> list =
        api.getAuthorizationsByParams(Map.of("groupIdIn", "group1", "resourceId", "PROC"));

    assertThat(list).hasSize(1);
  }

  @Test
  void testGetProcessDefinitionAuth_success() {
    stubFor(
        get(urlPathEqualTo("/api/authorization"))
            .withQueryParam("groupIdIn", equalTo("grp"))
            .withQueryParam("resourceType", equalTo("1"))
            .withQueryParam("resourceId", equalTo("PROC"))
            .willReturn(
                okJson(
                    """
                                        [
                                          {"id":"A5"}
                                        ]
                                        """)));

    List<Map> list = api.getProcessDefinitionAuth("grp", "PROC", 1);

    assertThat(list).hasSize(1);
  }

  @Test
  void testGetAuthorizationsByResource_userIdBranch() {
    stubFor(
        get(urlPathEqualTo("/api/authorization"))
            .withQueryParam("userIdIn", equalTo("userX"))
            .withQueryParam("resourceType", equalTo("1"))
            .withQueryParam("resourceId", equalTo("PROC"))
            .willReturn(okJson("[{\"id\":\"A6\"}]")));

    AuthorizationRequest req =
        AuthorizationRequest.builder().userId("userX").resourceType(1).resourceId("PROC").build();

    List<Map> list = api.getAuthorizationsByResource(req);

    assertThat(list).hasSize(1);
    assertThat(list.get(0)).containsEntry("id", "A6");
  }

  @Test
  void testGetAuthorizationsByResource_groupIdBranch() {
    stubFor(
        get(urlPathEqualTo("/api/authorization"))
            .withQueryParam("groupIdIn", equalTo("groupX"))
            .withQueryParam("resourceType", equalTo("1"))
            .withQueryParam("resourceId", equalTo("PROC"))
            .willReturn(okJson("[{\"id\":\"A7\"}]")));

    AuthorizationRequest req =
        AuthorizationRequest.builder().groupId("groupX").resourceType(1).resourceId("PROC").build();

    List<Map> list = api.getAuthorizationsByResource(req);

    assertThat(list).hasSize(1);
    assertThat(list.get(0)).containsEntry("id", "A7");
  }

  @Test
  void testUpdateAuthorization_success() {
    stubFor(put(urlEqualTo("/api/authorization/AUTH2")).willReturn(noContent()));

    AuthorizationRequest req =
        AuthorizationRequest.builder()
            .resourceType(1)
            .resourceId("PROC")
            .permissions(List.of("1"))
            .build();

    api.updateAuthorization("AUTH2", req);

    verify(putRequestedFor(urlEqualTo("/api/authorization/AUTH2")));
  }

  @Test
  void testDeleteAuthorization_success() {
    stubFor(delete(urlEqualTo("/api/authorization/AUTH3")).willReturn(noContent()));

    api.deleteAuthorization("AUTH3");

    verify(deleteRequestedFor(urlEqualTo("/api/authorization/AUTH3")));
  }
}
