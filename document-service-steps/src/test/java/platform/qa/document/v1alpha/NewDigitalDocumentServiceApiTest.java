package platform.qa.document.v1alpha;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.entity.ErrorResponse;
import platform.qa.entity.UploadDocumentResponse;
import platform.qa.v1alpha.document.NewDigitalDocumentServiceApi;

@DisplayName("NewDigitalDocumentServiceApi UNIT tests")
class NewDigitalDocumentServiceApiTest {

  private NewDigitalDocumentServiceApi api;
  private RequestSpecification spec;
  private Response response;
  private File file;
  private MockedStatic<io.restassured.RestAssured> restAssuredMock;

  @BeforeEach
  void setup() throws Exception {

    User user = User.builder().token("token-123").build();
    Service service = new Service("http://test", user);

    api = new NewDigitalDocumentServiceApi(service);

    spec = mock(RequestSpecification.class, RETURNS_SELF);
    response = mock(Response.class);

    restAssuredMock = mockStatic(io.restassured.RestAssured.class);
    restAssuredMock.when(io.restassured.RestAssured::given).thenReturn(spec);

    file = File.createTempFile("doc", ".txt");
  }

  @AfterEach
  void tearDown() {
    restAssuredMock.close();
  }

  // ================= uploadDocument(File) =================

  @Test
  void uploadDocument_success() {

    UploadDocumentResponse dto = new UploadDocumentResponse();
    dto.setId("123");

    mockFlow(dto, 200);

    UploadDocumentResponse result = api.uploadDocument(file);

    assertThat(result.getId()).isEqualTo("123");

    verify(spec).post("/v1alpha/documents");
  }

  @Test
  void uploadDocument_throwsIOException() {

    try (MockedStatic<Files> fs = mockStatic(Files.class)) {

      fs.when(() -> Files.probeContentType(any(Path.class))).thenThrow(new IOException("boom"));

      assertThatThrownBy(() -> api.uploadDocument(file))
          .isInstanceOf(IOException.class)
          .hasMessage("boom");
    }
  }

  // ================= uploadDocument(File, String) =================

  @Test
  void uploadWithCustomName_success() {

    UploadDocumentResponse dto = new UploadDocumentResponse();
    dto.setId("321");

    mockFlow(dto, 200);

    UploadDocumentResponse result = api.uploadDocument(file, "custom.pdf");

    assertThat(result.getId()).isEqualTo("321");

    verify(spec).multiPart("filename", "custom.pdf");
  }

  // ================= uploadDocument(File, status) =================

  @Test
  void upload_errorScenario() {

    ErrorResponse err = new ErrorResponse();
    err.setCode("BAD");

    mockFlow(err, 400);

    ErrorResponse result = api.uploadDocument(file, 400);

    assertThat(result.getCode()).isEqualTo("BAD");
  }

  // ================= uploadDocument(File, contentType, status) =================

  @Test
  void upload_withCustomContentType() {

    ErrorResponse err = new ErrorResponse();
    err.setMessage("wrong type");

    mockFlow(err, 415);

    ErrorResponse result = api.uploadDocument(file, "app/custom", 415);

    assertThat(result.getMessage()).isEqualTo("wrong type");

    verify(spec).multiPart(eq("file"), eq(file), eq("app/custom"));
  }

  // ================= download =================

  @Test
  void download_success() {

    byte[] bytes = "data".getBytes();

    mockDownload(bytes);

    byte[] result = api.downloadDocumentAsByteArray("id");

    assertThat(result).isEqualTo(bytes);

    verify(spec).get("/v1alpha/documents/{id}");
  }

  @Test
  void download_error() {

    ErrorResponse err = new ErrorResponse();
    err.setCode("404");

    mockFlow(err, 404);

    ErrorResponse result = api.downloadDocument("id", 404);

    assertThat(result.getCode()).isEqualTo("404");
  }

  @Test
  void download_withoutPathParam() {

    ErrorResponse err = new ErrorResponse();
    err.setCode("405");

    mockFlow(err, 405);

    ErrorResponse result = api.downloadDocumentWithOutPathParam(405);

    assertThat(result.getCode()).isEqualTo("405");

    verify(spec).get("/v1alpha/documents");
  }

  // ================= delete =================

  @Test
  void delete_success() {
    mockDelete();

    api.deleteDocument("id");

    verify(spec).delete("/v1alpha/documents/{id}");
  }

  @Test
  void delete_errorScenario() {
    ErrorResponse err = new ErrorResponse();
    err.setCode("FORBIDDEN");
    err.setMessage("You shall not pass");

    mockFlow(err, 403);

    ErrorResponse result = api.deleteDocument("id", 403);

    assertThat(result).isNotNull();
    assertThat(result.getCode()).isEqualTo("FORBIDDEN");
    assertThat(result.getMessage()).isEqualTo("You shall not pass");

    verify(spec).pathParam("id", "id");
    verify(spec).delete("/v1alpha/documents/{id}");
  }

  // ================= helpers =================

  private <T> void mockFlow(T body, int status) {
    ValidatableResponse validatable = mock(ValidatableResponse.class);
    ExtractableResponse<Response> extractable = mock(ExtractableResponse.class);

    when(spec.post(anyString())).thenReturn(response);
    when(spec.get(anyString())).thenReturn(response);
    when(spec.delete(anyString())).thenReturn(response);

    when(response.then()).thenReturn(validatable);
    when(validatable.statusCode(status)).thenReturn(validatable);
    when(validatable.extract()).thenReturn(extractable);

    when(extractable.as(any(Class.class))).thenReturn(body);
  }

  private void mockDownload(byte[] bytes) {
    ValidatableResponse validatable = mock(ValidatableResponse.class);
    ExtractableResponse<Response> extractable = mock(ExtractableResponse.class);

    when(spec.get(anyString())).thenReturn(response);

    when(response.then()).thenReturn(validatable);
    when(validatable.statusCode(200)).thenReturn(validatable);
    when(validatable.extract()).thenReturn(extractable);

    when(extractable.asByteArray()).thenReturn(bytes);
  }

  private void mockDelete() {
    ValidatableResponse validatable = mock(ValidatableResponse.class);

    when(spec.delete(anyString())).thenReturn(response);

    when(response.then()).thenReturn(validatable);
    when(validatable.statusCode(204)).thenReturn(validatable);
  }
}
