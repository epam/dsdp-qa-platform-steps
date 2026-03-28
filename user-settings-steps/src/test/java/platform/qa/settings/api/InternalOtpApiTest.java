package platform.qa.settings.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.settings.pojo.request.OtpData;

class InternalOtpApiTest {

  private InternalOtpApi internalOtpApi;

  @BeforeEach
  void setUp() {
    Service mockService = mock(Service.class);
    User mockUser = mock(User.class);

    when(mockService.getUrl()).thenReturn("https://user-settings.example.com");
    when(mockService.getUser()).thenReturn(mockUser);
    when(mockUser.getToken()).thenReturn("test-access-token");

    internalOtpApi = new InternalOtpApi(mockService);
  }

  @Test
  void getOtpDataShouldWork() {
    try (MockedStatic<io.restassured.RestAssured> mocked =
        mockStatic(io.restassured.RestAssured.class)) {

      RequestSpecification requestSpecMock = mock(RequestSpecification.class);
      Response responseMock = mock(Response.class);
      ValidatableResponse validatableResponseMock = mock(ValidatableResponse.class);
      ExtractableResponse<Response> extractableResponseMock = mock(ExtractableResponse.class);

      mocked.when(io.restassured.RestAssured::given).thenReturn(requestSpecMock);

      when(requestSpecMock.spec(any())).thenReturn(requestSpecMock);
      when(requestSpecMock.pathParam(anyString(), any())).thenReturn(requestSpecMock);
      when(requestSpecMock.get(anyString())).thenReturn(responseMock);

      when(responseMock.then()).thenReturn(validatableResponseMock);
      when(validatableResponseMock.statusCode(anyInt())).thenReturn(validatableResponseMock);
      when(validatableResponseMock.extract()).thenReturn(extractableResponseMock);

      when(extractableResponseMock.as(OtpData.class)).thenReturn(new OtpData());

      OtpData result = internalOtpApi.getOtpData("user1", "EMAIL");

      assertThat(result).isNotNull();
    }
  }

  @Test
  void createOrUpdateOtpDataShouldWork() {
    try (MockedStatic<io.restassured.RestAssured> mocked =
        mockStatic(io.restassured.RestAssured.class)) {

      RequestSpecification requestSpecMock = mock(RequestSpecification.class);
      Response responseMock = mock(Response.class);
      ValidatableResponse validatableResponseMock = mock(ValidatableResponse.class);
      ExtractableResponse<Response> extractableResponseMock = mock(ExtractableResponse.class);

      mocked.when(io.restassured.RestAssured::given).thenReturn(requestSpecMock);

      when(requestSpecMock.spec(any())).thenReturn(requestSpecMock);
      when(requestSpecMock.pathParam(anyString(), any())).thenReturn(requestSpecMock);
      when(requestSpecMock.body(any(OtpData.class))).thenReturn(requestSpecMock);
      when(requestSpecMock.put(anyString())).thenReturn(responseMock);

      when(responseMock.then()).thenReturn(validatableResponseMock);
      when(validatableResponseMock.statusCode(anyInt())).thenReturn(validatableResponseMock);
      when(validatableResponseMock.extract()).thenReturn(extractableResponseMock);

      when(extractableResponseMock.as(OtpData.class)).thenReturn(new OtpData());

      OtpData result = internalOtpApi.createOrUpdateOtpData("user1", "EMAIL", new OtpData());

      assertThat(result).isNotNull();
    }
  }

  @Test
  void deleteOtpDataShouldWork() {
    try (MockedStatic<io.restassured.RestAssured> mocked =
        mockStatic(io.restassured.RestAssured.class)) {

      RequestSpecification requestSpecMock = mock(RequestSpecification.class);
      Response responseMock = mock(Response.class);
      ValidatableResponse validatableResponseMock = mock(ValidatableResponse.class);

      mocked.when(io.restassured.RestAssured::given).thenReturn(requestSpecMock);

      when(requestSpecMock.spec(any())).thenReturn(requestSpecMock);
      when(requestSpecMock.pathParam(anyString(), any())).thenReturn(requestSpecMock);
      when(requestSpecMock.delete(anyString())).thenReturn(responseMock);

      when(responseMock.then()).thenReturn(validatableResponseMock);
      when(validatableResponseMock.statusCode(anyInt())).thenReturn(validatableResponseMock);

      internalOtpApi.deleteOtpData("user1", "EMAIL");

      verify(requestSpecMock).delete(anyString());
    }
  }
}
