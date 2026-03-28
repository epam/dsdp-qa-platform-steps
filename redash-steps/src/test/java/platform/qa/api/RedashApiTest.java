package platform.qa.api;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.restassured.RestAssured;
import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import platform.qa.clients.RedashClient;
import platform.qa.entities.IEntity;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.redash.*;

class RedashApiTest {

  private RedashApi api;
  private RedashClient client;
  private Service service;

  private MockedStatic<RestAssured> restAssuredMock;

  private static void setField(Object target, String field, Object value) {
    try {
      var f = target.getClass().getDeclaredField(field);
      f.setAccessible(true);
      f.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  void setup() {
    client = mock(RedashClient.class);
    User user = new User("usr", "pwd");
    user.setToken("token");
    service = new Service("http://test", user);

    api = new RedashApi(service);
    setField(api, "redashClient", client);

    restAssuredMock = mockStatic(RestAssured.class);
  }

  @AfterEach
  void tearDown() {
    restAssuredMock.close();
  }

  // ======================================================================
  // GET DASHBOARDS / GET QUERIES
  // ======================================================================
  @Test
  void testGetDashboardsList_success() {
    List<Map<String, Object>> raw =
        List.of(Map.of("name", "Dash1", "id", 1), Map.of("name", "Dash2", "id", 2));

    when(client.getRequest("/dashboards", "results")).thenReturn(raw);

    Map<String, Integer> result = api.getDashboardsList();

    assertThat(result).containsEntry("Dash1", 1).containsEntry("Dash2", 2);
  }

  @Test
  void testGetQueriesList_success() {
    List<Map<String, Object>> raw = List.of(Map.of("name", "Q1", "id", 10));

    when(client.getRequest("/queries", "results")).thenReturn(raw);

    Map<String, Integer> result = api.getQueriesList();

    assertThat(result).containsEntry("Q1", 10);
  }

  // ======================================================================
  // GET DATA SOURCES LIST
  // ======================================================================
  @Test
  void testGetDataSourcesList() {
    when(client.getRequest("/data_sources", "name")).thenReturn(List.of("DS1", "DS2"));

    assertThat(api.getDataSourcesList()).containsExactly("DS1", "DS2");
  }

  // ======================================================================
  // GET GROUPS LIST
  // ======================================================================
  @Test
  void testGetGroupsList() {
    when(client.getRequest("/groups", "name")).thenReturn(List.of("Admins", "Users"));

    assertThat(api.getGroupsList()).containsExactly("Admins", "Users");
  }

  // ======================================================================
  // createDashboardWithTextbox
  // ======================================================================
  @Test
  void testCreateDashboardWithTextbox_success() {

    Map<String, Object> created = Map.of("id", 55);
    when(client.postRequest(eq("/dashboards"), any(CreateDashboardRequest.class)))
        .thenReturn(created);

    when(client.postRequest(eq("/widgets"), any(CreateTextboxRequest.class))).thenReturn(null);

    when(client.postRequest(eq("/dashboards/55"), any(PublishDashboardRequest.class)))
        .thenReturn(null);

    int id = api.createDashboardWithTextbox("MyDash");

    assertThat(id).isEqualTo(55);

    verify(client).postRequest(eq("/dashboards"), any(CreateDashboardRequest.class));
    verify(client).postRequest(eq("/widgets"), any(CreateTextboxRequest.class));
    verify(client).postRequest(eq("/dashboards/55"), any(PublishDashboardRequest.class));
  }

  // ======================================================================
  // deleteDashboardWithDashboardId
  // ======================================================================
  @Test
  void testDeleteDashboardWithDashboardId() {
    api.deleteDashboardWithDashboardId("Name", 99);
    verify(client).deleteRequest("/dashboards/99");
  }

  // ======================================================================
  // executeQueryOnRedash
  // ======================================================================
  @Test
  void testExecuteQueryOnRedash_success() {
    when(client.postRequest(eq("/queries/777/results"), any())).thenReturn(Map.of("ok", true));

    Map<String, Object> result =
        api.executeQueryOnRedash("777", Map.of("p", "v"), mock(IEntity.class));

    assertThat(result).containsEntry("ok", true);
  }

  // ======================================================================
  // registerUser
  // ======================================================================
  @Test
  void testRegisterUser_success() {
    RedashUser user = new RedashUser();
    user.setName("alice");

    // user does NOT exist
    when(client.getRequest("users?order=-created_at", "results")).thenReturn(List.of());

    // create user → response contains invite_link
    when(client.postRequest(eq("/users?no_invite"), any(RedashUser.class)))
        .thenReturn(Map.of("invite_link", "/invite/link"));

    // accept invite (void method!)
    doNothing().when(client).postRequestToSpecificUrl(eq("/invite/link"), any());

    // after creation: user list contains our user
    when(client.getRequest("users?order=-created_at", "results"))
        .thenReturn(List.of(Map.of("name", "alice", "id", 101)));

    // refresh token
    when(client.postRequest(eq("/users/101/regenerate_api_key"), any()))
        .thenReturn(Map.of("api_key", "KEY999"));

    RedashUser result = api.registerUser(user);

    assertThat(result.getId()).isEqualTo(101);
    assertThat(result.getToken()).isEqualTo("KEY999");
  }

  // ======================================================================
  // assignRoleToUser
  // ======================================================================
  @Test
  void testAssignRoleToUser() {
    RedashUser user = new RedashUser();
    user.setId(10);
    user.setName("alice");

    List<Map<String, Object>> groups =
        List.of(Map.of("name", "admin", "id", 1), Map.of("name", "user", "id", 2));

    when(client.getRequest("/groups", "")).thenReturn(groups);

    api.assignRoleToUser(user, List.of("admin", "user"));

    verify(client).postRequest(eq("/groups/1/members"), any(CreateRoleRequest.class));
    verify(client).postRequest(eq("/groups/2/members"), any(CreateRoleRequest.class));
  }

  // ======================================================================
  // getDataSourceToId
  // ======================================================================
  @Test
  void testGetDataSourceToId() {
    List<Map<String, Object>> raw =
        List.of(Map.of("name", "ds1", "id", "11"), Map.of("name", "ds2", "id", "22"));

    when(client.getRequest("/data_sources", "")).thenReturn(raw);

    Map<String, Integer> res = api.getDataSourceToId();
    assertThat(res).containsEntry("ds1", 11).containsEntry("ds2", 22);
  }

  // ======================================================================
  // getSchemaNameForDataSource
  // ======================================================================
  @Test
  void testGetSchemaNameForDataSource_success() {
    List<Map<String, Object>> raw = List.of(Map.of("name", "schema1"), Map.of("name", "schema2"));

    when(client.getRequest("data_sources/5/schema", "schema")).thenReturn(raw);

    assertThat(api.getSchemaNameForDataSource(5)).containsExactly("schema1", "schema2");
  }

  @Test
  void testGetSchemaNameForDataSource_empty() {
    when(client.getRequest("data_sources/7/schema", "schema")).thenReturn(null);

    assertThat(api.getSchemaNameForDataSource(7)).isEmpty();
  }
}
