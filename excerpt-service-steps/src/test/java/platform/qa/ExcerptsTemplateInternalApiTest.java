package platform.qa;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.request.ExcerptsTemplateInternalRequest;
import platform.qa.pojo.response.ExcerptsTemplateInternalResponse;

@DisplayName("ExcerptsTemplateInternalApi Integration Tests")
public class ExcerptsTemplateInternalApiTest {

  private static WireMockServer wireMockServer;
  private ExcerptsTemplateInternalApi api;
  private Service service;

  @BeforeAll
  public static void setupClass() {
    wireMockServer =
        new WireMockServer(WireMockConfiguration.options().port(8091).bindAddress("localhost"));
    wireMockServer.start();
    configureFor("localhost", 8091);
  }

  @AfterAll
  public static void tearDownClass() {
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
  }

  @BeforeEach
  public void setup() {
    wireMockServer.resetAll();
    configureFor("localhost", 8091);

    // Create real Service and User objects - NO MOCKS
    User user = User.builder().token("test-token-456").build();
    service = new Service("http://localhost:8091", user);

    // Create real API instance - NO MOCKS
    api = new ExcerptsTemplateInternalApi(service);
  }

  @Nested
  @DisplayName("Create Excerpt Template - Success Scenarios")
  class CreateExcerptTemplateSuccessTests {

    @Test
    @DisplayName("Should create excerpt template successfully")
    public void shouldCreateExcerptTemplateSuccessfully() {
      ExcerptsTemplateInternalRequest request =
          ExcerptsTemplateInternalRequest.builder()
              .templateName("Test Template")
              .template("Test Template Content")
              .templateType("HTML")
              .checksum("abc123")
              .createdAt("2023-01-01T00:00:00Z")
              .updatedAt("2023-01-01T00:00:00Z")
              .build();

      // Setup WireMock stub
      stubFor(
          post(urlPathEqualTo("/internal-api/excerpts"))
              .willReturn(
                  aResponse()
                      .withStatus(201)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "id": "template-123",
                                        "templateName": "Test Template",
                                        "template": "Test Template Content",
                                        "templateType": "HTML",
                                        "checksum": "abc123",
                                        "createdAt": "2023-01-01T00:00:00Z",
                                        "updatedAt": "2023-01-01T00:00:00Z"
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsTemplateInternalResponse response = api.createExcerptTemplate(request);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getId()).isEqualTo("template-123");
      assertThat(response.getTemplateName()).isEqualTo("Test Template");
      assertThat(response.getTemplate()).isEqualTo("Test Template Content");
      assertThat(response.getTemplateType()).isEqualTo("HTML");
      assertThat(response.getChecksum()).isEqualTo("abc123");
      assertThat(response.getCreatedAt()).isEqualTo("2023-01-01T00:00:00Z");
      assertThat(response.getUpdatedAt()).isEqualTo("2023-01-01T00:00:00Z");

      // Verify request was made correctly
      verify(
          postRequestedFor(urlPathEqualTo("/internal-api/excerpts"))
              .withHeader("Content-Type", equalTo("application/json"))
              .withHeader("X-Access-Token", equalTo("test-token-456"))
              .withHeader("X-XSRF-TOKEN", equalTo("Token"))
              .withHeader("Cookie", equalTo("XSRF-TOKEN=Token")));
    }

    @Test
    @DisplayName("Should create excerpt template with minimal data")
    public void shouldCreateExcerptTemplateWithMinimalData() {
      ExcerptsTemplateInternalRequest request =
          ExcerptsTemplateInternalRequest.builder().templateName("Minimal Template").build();

      // Setup WireMock stub
      stubFor(
          post(urlPathEqualTo("/internal-api/excerpts"))
              .willReturn(
                  aResponse()
                      .withStatus(201)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "id": "minimal-template-456",
                                        "templateName": "Minimal Template"
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsTemplateInternalResponse response = api.createExcerptTemplate(request);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getId()).isEqualTo("minimal-template-456");
      assertThat(response.getTemplateName()).isEqualTo("Minimal Template");
      assertThat(response.getTemplate()).isNull();
      assertThat(response.getTemplateType()).isNull();
      assertThat(response.getChecksum()).isNull();
    }

    @Test
    @DisplayName("Should create excerpt template with different template types")
    public void shouldCreateExcerptTemplateWithDifferentTemplateTypes() {
      String[] templateTypes = {"HTML", "PDF", "XML", "DOCX"};

      for (String templateType : templateTypes) {
        wireMockServer.resetAll();

        ExcerptsTemplateInternalRequest request =
            ExcerptsTemplateInternalRequest.builder()
                .templateName("Template for " + templateType)
                .template("Content for " + templateType)
                .templateType(templateType)
                .checksum("checksum-" + templateType.toLowerCase())
                .build();

        // Setup WireMock stub
        stubFor(
            post(urlEqualTo("/internal-api/excerpts"))
                .willReturn(
                    aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            String.format(
                                """
                                        {
                                            "id": "template-%s",
                                            "templateName": "Template for %s",
                                            "template": "Content for %s",
                                            "templateType": "%s",
                                            "checksum": "checksum-%s"
                                        }
                                        """,
                                templateType.toLowerCase(),
                                templateType,
                                templateType,
                                templateType,
                                templateType.toLowerCase()))));

        // Execute REAL method call
        ExcerptsTemplateInternalResponse response = api.createExcerptTemplate(request);

        // Verify
        assertThat(response).isNotNull();
        assertThat(response.getTemplateType()).isEqualTo(templateType);
        assertThat(response.getTemplateName()).isEqualTo("Template for " + templateType);
      }
    }
  }

  @Nested
  @DisplayName("Get Excerpt Template By ID - Success Scenarios")
  class GetExcerptTemplateByIdSuccessTests {

    @Test
    @DisplayName("Should get excerpt template by id successfully")
    public void shouldGetExcerptTemplateByIdSuccessfully() {
      String templateId = "template-789";

      // Setup WireMock stub
      stubFor(
          get(urlPathEqualTo("/internal-api/excerpts/" + templateId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "id": "template-789",
                                        "templateName": "Retrieved Template",
                                        "template": "Retrieved Template Content",
                                        "templateType": "PDF",
                                        "checksum": "def456",
                                        "createdAt": "2023-02-01T00:00:00Z",
                                        "updatedAt": "2023-02-01T00:00:00Z"
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsTemplateInternalResponse response = api.getExcerptTemplateById(templateId);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getId()).isEqualTo("template-789");
      assertThat(response.getTemplateName()).isEqualTo("Retrieved Template");
      assertThat(response.getTemplate()).isEqualTo("Retrieved Template Content");
      assertThat(response.getTemplateType()).isEqualTo("PDF");
      assertThat(response.getChecksum()).isEqualTo("def456");
      assertThat(response.getCreatedAt()).isEqualTo("2023-02-01T00:00:00Z");
      assertThat(response.getUpdatedAt()).isEqualTo("2023-02-01T00:00:00Z");

      // Verify request was made correctly
      verify(
          getRequestedFor(urlPathEqualTo("/internal-api/excerpts/" + templateId))
              .withHeader("X-Access-Token", equalTo("test-token-456"))
              .withHeader("X-XSRF-TOKEN", equalTo("Token"))
              .withHeader("Cookie", equalTo("XSRF-TOKEN=Token")));
    }

    @Test
    @DisplayName("Should get excerpt template with UUID id")
    public void shouldGetExcerptTemplateWithUuidId() {
      String templateId = "550e8400-e29b-41d4-a716-446655440000";

      // Setup WireMock stub
      stubFor(
          get(urlPathEqualTo("/internal-api/excerpts/" + templateId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                        "templateName": "UUID Template",
                                        "template": "UUID Template Content",
                                        "templateType": "XML",
                                        "checksum": "uuid123"
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsTemplateInternalResponse response = api.getExcerptTemplateById(templateId);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getId()).isEqualTo(templateId);
      assertThat(response.getTemplateName()).isEqualTo("UUID Template");
      assertThat(response.getTemplateType()).isEqualTo("XML");
    }

    @Test
    @DisplayName("Should handle empty response fields")
    public void shouldHandleEmptyResponseFields() {
      String templateId = "empty-fields-template";

      // Setup WireMock stub with empty/null fields
      stubFor(
          get(urlEqualTo("/internal-api/excerpts/" + templateId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "id": "empty-fields-template",
                                        "templateName": null,
                                        "template": "",
                                        "templateType": null,
                                        "checksum": null
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsTemplateInternalResponse response = api.getExcerptTemplateById(templateId);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getId()).isEqualTo("empty-fields-template");
      assertThat(response.getTemplateName()).isNull();
      assertThat(response.getTemplate()).isEmpty();
      assertThat(response.getTemplateType()).isNull();
      assertThat(response.getChecksum()).isNull();
    }
  }

  @Nested
  @DisplayName("Delete Excerpt Template By ID - Success Scenarios")
  class DeleteExcerptTemplateByIdSuccessTests {

    @Test
    @DisplayName("Should delete excerpt template by id successfully")
    public void shouldDeleteExcerptTemplateByIdSuccessfully() {
      String templateId = "template-to-delete";

      // Setup WireMock stub
      stubFor(
          delete(urlPathEqualTo("/internal-api/excerpts/" + templateId))
              .willReturn(aResponse().withStatus(204)));

      // Execute REAL method call - should not throw exception
      api.deleteExcerptTemplateById(templateId);

      // Verify request was made correctly
      verify(
          deleteRequestedFor(urlPathEqualTo("/internal-api/excerpts/" + templateId))
              .withHeader("X-Access-Token", equalTo("test-token-456"))
              .withHeader("X-XSRF-TOKEN", equalTo("Token"))
              .withHeader("Cookie", equalTo("XSRF-TOKEN=Token")));
    }

    @Test
    @DisplayName("Should delete excerpt template with special characters in id")
    public void shouldDeleteExcerptTemplateWithSpecialCharactersInId() {
      String templateId = "template-with-special-chars-encoded";

      // Setup WireMock stub
      stubFor(
          delete(urlPathEqualTo("/internal-api/excerpts/" + templateId))
              .willReturn(aResponse().withStatus(204)));

      // Execute REAL method call - should not throw exception
      api.deleteExcerptTemplateById(templateId);

      // Verify request was made correctly
      verify(deleteRequestedFor(urlPathEqualTo("/internal-api/excerpts/" + templateId)));
    }

    @Test
    @DisplayName("Should delete excerpt template with numeric id")
    public void shouldDeleteExcerptTemplateWithNumericId() {
      String templateId = "12345";

      // Setup WireMock stub
      stubFor(
          delete(urlPathEqualTo("/internal-api/excerpts/" + templateId))
              .willReturn(aResponse().withStatus(204)));

      // Execute REAL method call - should not throw exception
      api.deleteExcerptTemplateById(templateId);

      // Verify request was made correctly
      verify(deleteRequestedFor(urlPathEqualTo("/internal-api/excerpts/" + templateId)));
    }
  }

  @Nested
  @DisplayName("Request Headers and Method Verification")
  class RequestHeadersAndMethodTests {

    @Test
    @DisplayName("Should use correct HTTP methods for all operations")
    public void shouldUseCorrectHttpMethodsForAllOperations() {
      String templateId = "method-test-template";

      ExcerptsTemplateInternalRequest request =
          ExcerptsTemplateInternalRequest.builder().templateName("Method Test Template").build();

      // Setup WireMock stubs
      stubFor(
          post(urlPathEqualTo("/internal-api/excerpts"))
              .willReturn(
                  aResponse()
                      .withStatus(201)
                      .withHeader("Content-Type", "application/json")
                      .withBody("{\"id\":\"" + templateId + "\"}")));

      stubFor(
          get(urlPathEqualTo("/internal-api/excerpts/" + templateId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody("{\"id\":\"" + templateId + "\"}")));

      stubFor(
          delete(urlPathEqualTo("/internal-api/excerpts/" + templateId))
              .willReturn(aResponse().withStatus(204)));

      // Execute REAL method calls
      api.createExcerptTemplate(request);
      api.getExcerptTemplateById(templateId);
      api.deleteExcerptTemplateById(templateId);

      // Verify correct HTTP methods were used
      verify(postRequestedFor(urlPathEqualTo("/internal-api/excerpts")));
      verify(getRequestedFor(urlPathEqualTo("/internal-api/excerpts/" + templateId)));
      verify(deleteRequestedFor(urlPathEqualTo("/internal-api/excerpts/" + templateId)));
    }

    @Test
    @DisplayName("Should include all required headers for create operation")
    public void shouldIncludeAllRequiredHeadersForCreateOperation() {
      ExcerptsTemplateInternalRequest request =
          ExcerptsTemplateInternalRequest.builder().templateName("Header Test Template").build();

      // Setup WireMock stub
      stubFor(
          post(urlPathEqualTo("/internal-api/excerpts"))
              .willReturn(
                  aResponse()
                      .withStatus(201)
                      .withHeader("Content-Type", "application/json")
                      .withBody("{\"id\":\"header-test-id\"}")));

      // Execute REAL method call
      api.createExcerptTemplate(request);

      // Verify all required headers were sent
      verify(
          postRequestedFor(urlPathEqualTo("/internal-api/excerpts"))
              .withHeader("Content-Type", equalTo("application/json"))
              .withHeader("X-Access-Token", equalTo("test-token-456"))
              .withHeader("X-XSRF-TOKEN", equalTo("Token"))
              .withHeader("Cookie", equalTo("XSRF-TOKEN=Token")));
    }
  }

  @Nested
  @DisplayName("Full CRUD Integration Test")
  class FullCrudIntegrationTest {

    @Test
    @DisplayName("Should perform complete CRUD operations successfully")
    public void shouldPerformCompleteCrudOperationsSuccessfully() {
      String templateId = "crud-test-template";

      ExcerptsTemplateInternalRequest createRequest =
          ExcerptsTemplateInternalRequest.builder()
              .templateName("CRUD Test Template")
              .template("CRUD Test Content")
              .templateType("HTML")
              .checksum("crud123")
              .build();

      // Setup WireMock stubs for all operations
      stubFor(
          post(urlEqualTo("/internal-api/excerpts"))
              .willReturn(
                  aResponse()
                      .withStatus(201)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "id": "crud-test-template",
                                        "templateName": "CRUD Test Template",
                                        "template": "CRUD Test Content",
                                        "templateType": "HTML",
                                        "checksum": "crud123"
                                    }
                                    """)));

      stubFor(
          get(urlEqualTo("/internal-api/excerpts/" + templateId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "id": "crud-test-template",
                                        "templateName": "CRUD Test Template",
                                        "template": "CRUD Test Content",
                                        "templateType": "HTML",
                                        "checksum": "crud123"
                                    }
                                    """)));

      stubFor(
          delete(urlEqualTo("/internal-api/excerpts/" + templateId))
              .willReturn(aResponse().withStatus(204)));

      // Execute REAL method calls - CREATE
      ExcerptsTemplateInternalResponse createResponse = api.createExcerptTemplate(createRequest);
      assertThat(createResponse).isNotNull();
      assertThat(createResponse.getId()).isEqualTo(templateId);

      // Execute REAL method calls - READ
      ExcerptsTemplateInternalResponse getResponse = api.getExcerptTemplateById(templateId);
      assertThat(getResponse).isNotNull();
      assertThat(getResponse.getId()).isEqualTo(templateId);
      assertThat(getResponse.getTemplateName()).isEqualTo("CRUD Test Template");

      // Execute REAL method calls - DELETE
      api.deleteExcerptTemplateById(templateId);

      // Verify all operations were called
      verify(postRequestedFor(urlEqualTo("/internal-api/excerpts")));
      verify(getRequestedFor(urlEqualTo("/internal-api/excerpts/" + templateId)));
      verify(deleteRequestedFor(urlEqualTo("/internal-api/excerpts/" + templateId)));
    }
  }
}
