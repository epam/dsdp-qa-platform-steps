package platform.qa.steps;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.restassured.response.Response;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import platform.qa.api.FormSchemaProviderApi;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.entity.ErrorMessageResponse;

class FormSchemaProviderStepsTest {

  private FormSchemaProviderApi apiMock;
  private FormSchemaProviderSteps steps;

  @BeforeEach
  void setup() {
    apiMock = mock(FormSchemaProviderApi.class);

    User user = new User("user", "pwd");
    user.setToken("TEST_TOKEN");
    Service dummy = new Service("http://fake", user);
    steps = new FormSchemaProviderSteps(dummy);

    var field = FormSchemaProviderSteps.class.getDeclaredFields()[1];
    field.setAccessible(true);
    try {
      field.set(steps, apiMock);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // ------------------------------------------------------------------
  // SUCCESS: form found
  // ------------------------------------------------------------------
  @Test
  void testGetFormByName_success() {
    Response resp = mock(Response.class);
    when(resp.statusCode()).thenReturn(200);
    when(resp.body()).thenReturn(resp);
    when(resp.as(Map.class)).thenReturn(Map.of("name", "MyForm"));

    when(apiMock.getSearchFormByName("MyForm")).thenReturn(resp);

    Map result = steps.getFormByName("MyForm");

    assertThat(result).containsEntry("name", "MyForm");
  }

  // ------------------------------------------------------------------
  // NOT FOUND with correct error type -> return emptyMap
  // ------------------------------------------------------------------
  @Test
  void testGetFormByName_notFoundReturnsEmptyMap() {
    Response resp = mock(Response.class);

    ErrorMessageResponse err = new ErrorMessageResponse();
    err.setCode("FORM_SCHEMA_NOT_FOUND");

    when(resp.statusCode()).thenReturn(404);
    when(resp.body()).thenReturn(resp);
    when(resp.as(ErrorMessageResponse.class)).thenReturn(err);

    when(apiMock.getSearchFormByName("Missing")).thenReturn(resp);

    Map result = steps.getFormByName("Missing");

    assertThat(result).isEqualTo(Collections.emptyMap());
  }

  // ------------------------------------------------------------------
  // NOT FOUND but wrong error → throws IllegalStateException
  // ------------------------------------------------------------------
  @Test
  void testGetFormByName_notFoundWrongError_throws() {
    Response resp = mock(Response.class);

    ErrorMessageResponse err = new ErrorMessageResponse();
    err.setCode("OTHER_ERROR");

    when(resp.statusCode()).thenReturn(404);
    when(resp.body()).thenReturn(resp);
    when(resp.as(ErrorMessageResponse.class)).thenReturn(err);
    when(resp.prettyPrint()).thenReturn("Bad things happened");

    when(apiMock.getSearchFormByName("Bad")).thenReturn(resp);

    assertThatThrownBy(() -> steps.getFormByName("Bad"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Bad things happened");
  }

  // ------------------------------------------------------------------
  // OTHER STATUS → throws IllegalStateException
  // ------------------------------------------------------------------
  @Test
  void testGetFormByName_unknownStatus_throws() {
    Response resp = mock(Response.class);

    when(resp.statusCode()).thenReturn(500);
    when(resp.prettyPrint()).thenReturn("Internal error");

    when(apiMock.getSearchFormByName("X")).thenReturn(resp);

    assertThatThrownBy(() -> steps.getFormByName("X"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Internal error");
  }

  // ------------------------------------------------------------------
  // isFormWasFoundByName TRUE
  // ------------------------------------------------------------------
  @Test
  void testIsFormWasFoundByName_true() {
    Response resp = mock(Response.class);

    when(resp.statusCode()).thenReturn(200);
    when(resp.body()).thenReturn(resp);
    when(resp.as(Map.class)).thenReturn(Map.of("name", "MyForm"));

    when(apiMock.getSearchFormByName("MyForm")).thenReturn(resp);

    assertThat(steps.isFormWasFoundByName("MyForm")).isTrue();
  }

  // ------------------------------------------------------------------
  // isFormWasFoundByName FALSE
  // ------------------------------------------------------------------
  @Test
  void testIsFormWasFoundByName_false() {
    Response resp = mock(Response.class);

    when(resp.statusCode()).thenReturn(200);
    when(resp.body()).thenReturn(resp);
    when(resp.as(Map.class)).thenReturn(Map.of("name", "OtherForm"));

    when(apiMock.getSearchFormByName("MyForm")).thenReturn(resp);

    assertThat(steps.isFormWasFoundByName("MyForm")).isFalse();
  }
}
