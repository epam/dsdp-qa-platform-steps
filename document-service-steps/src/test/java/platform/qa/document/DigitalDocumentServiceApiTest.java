package platform.qa.document;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.response.Response;
import java.io.File;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import platform.qa.DigitalDocumentServiceApi;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.entity.ErrorResponse;
import platform.qa.entity.UploadDocumentResponse;

@ExtendWith(WireMockExtension.class)
public class DigitalDocumentServiceApiTest {
  @RegisterExtension
  static WireMockExtension wm =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  DigitalDocumentServiceApi api;

  @BeforeEach
  void setup() {
    WireMock.configureFor("localhost", wm.getPort());

    User user = new User("u", "p");
    user.setToken("TOKEN");

    Service service = new Service(wm.baseUrl() + "/", user);
    api = new DigitalDocumentServiceApi(service);
  }

  @Test
  void uploadDocument_success() throws Exception {
    stubFor(
        post(urlPathMatching("/documents/.*/.*/.*"))
            .willReturn(
                okJson(
                    """
                { "id": "DOC1" }
              """)));

    File file = File.createTempFile("test", ".txt");
    Files.writeString(file.toPath(), "hello");

    UploadDocumentResponse response = api.uploadDocument(file, "PI", "TASK", "field", "file1");

    assertThat(response.getId()).isEqualTo("DOC1");
  }

  @Test
  void uploadDocument_error() throws Exception {
    stubFor(
        post(urlPathMatching("/documents/.*/.*/.*"))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                    { "code": "ERR", "message": "bad file" }
                  """)));

    File file = File.createTempFile("bad", ".exe");

    ErrorResponse error = api.uploadDocument(file, "PI", "TASK", "field", 400, "file1");

    assertThat(error.getMessage()).contains("bad");
  }

  @Test
  void downloadDocument_success() {
    byte[] data = "PDFDATA".getBytes();

    stubFor(get(urlMatching("/documents/.*/.*/.*/.*")).willReturn(ok().withBody(data)));

    byte[] result = api.downloadDocumentAsByteArray("PI", "TASK", "field", "DOC1");

    assertThat(result).isEqualTo(data);
  }

  @Test
  void downloadDocument_error() {
    stubFor(
        get(urlMatching("/documents/.*/.*/.*/.*"))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                    { "code": "NOT_FOUND" }
                  """)));

    ErrorResponse error = api.downloadDocument("PI", "TASK", "field", "DOC1", 404);

    assertThat(error.getCode()).isEqualTo("NOT_FOUND");
  }

  @Test
  void searchDocuments_success() {
    stubFor(
        post(urlMatching("/documents/.*/.*/search"))
            .willReturn(
                okJson(
                    """
            { "total": 1 }
          """)));

    Response response = api.searchDocuments("PI", "TASK", "{}");

    assertThat(response.jsonPath().getInt("total")).isEqualTo(1);
  }

  @Test
  void appendFileIds_empty() {
    assertThat(api.appendFileIds("", "A")).isEqualTo("A");
  }

  @Test
  void appendFileIds_notEmpty() {
    assertThat(api.appendFileIds("A", "B")).isEqualTo("A,B");
  }

  @Test
  void deleteDocument_success() {
    stubFor(delete(urlMatching("/documents/.*/.*/.*/.*")).willReturn(ok()));

    api.deleteDocumentById("PI", "TASK", "field", "DOC1");

    verify(deleteRequestedFor(anyUrl()));
  }
}
