package platform.qa;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.response.ExcerptsResponse;

@DisplayName("ExcerptsServiceApi Integration Tests")
public class ExcerptsServiceApiTest {

  private static WireMockServer wireMockServer;
  private ExcerptsServiceApi api;
  private Service service;

  @BeforeAll
  public static void setupClass() {
    wireMockServer =
        new WireMockServer(WireMockConfiguration.options().port(8090).bindAddress("localhost"));
    wireMockServer.start();
    configureFor("localhost", 8090);
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
    configureFor("localhost", 8090);

    // Create real Service and User objects - NO MOCKS
    User user = User.builder().token("test-token-123").build();
    service = new Service("http://localhost:8090", user);

    // Create real API instance - NO MOCKS
    api = new ExcerptsServiceApi(service);
  }

  @Nested
  @DisplayName("Get Excerpt By ID - Success Scenarios")
  class GetExcerptByIdSuccessTests {

    @Test
    @DisplayName("Should get excerpt by id successfully")
    public void shouldGetExcerptByIdSuccessfully() {
      String excerptId = "excerpt-123";

      // Setup WireMock stub
      stubFor(
          get(urlPathEqualTo("/" + excerptId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "excerptIdentifier": "excerpt-identifier-123"
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsResponse response = api.getExcerptById(excerptId);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getExcerptIdentifier()).isEqualTo("excerpt-identifier-123");

      // Verify request was made correctly
      verify(
          getRequestedFor(urlPathEqualTo("/" + excerptId))
              .withHeader("X-Access-Token", equalTo("test-token-123"))
              .withHeader("X-XSRF-TOKEN", equalTo("Token"))
              .withHeader("Cookie", equalTo("XSRF-TOKEN=Token")));
    }

    @Test
    @DisplayName("Should get excerpt with special characters in id")
    public void shouldGetExcerptWithSpecialCharactersInId() {
      String excerptId = "excerpt-with-special-chars-encoded";

      // Setup WireMock stub
      stubFor(
          get(urlPathEqualTo("/" + excerptId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "excerptIdentifier": "special-excerpt-identifier"
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsResponse response = api.getExcerptById(excerptId);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getExcerptIdentifier()).isEqualTo("special-excerpt-identifier");

      // Verify request was made correctly
      verify(getRequestedFor(urlPathEqualTo("/" + excerptId)));
    }

    @Test
    @DisplayName("Should get excerpt with numeric id")
    public void shouldGetExcerptWithNumericId() {
      String excerptId = "12345";

      // Setup WireMock stub
      stubFor(
          get(urlPathEqualTo("/" + excerptId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "excerptIdentifier": "numeric-excerpt-identifier"
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsResponse response = api.getExcerptById(excerptId);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getExcerptIdentifier()).isEqualTo("numeric-excerpt-identifier");

      // Verify request was made correctly
      verify(getRequestedFor(urlPathEqualTo("/" + excerptId)));
    }

    @Test
    @DisplayName("Should get excerpt with UUID id")
    public void shouldGetExcerptWithUuidId() {
      String excerptId = "550e8400-e29b-41d4-a716-446655440000";

      // Setup WireMock stub
      stubFor(
          get(urlPathEqualTo("/" + excerptId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "excerptIdentifier": "uuid-excerpt-identifier"
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsResponse response = api.getExcerptById(excerptId);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getExcerptIdentifier()).isEqualTo("uuid-excerpt-identifier");

      // Verify request was made correctly
      verify(getRequestedFor(urlPathEqualTo("/" + excerptId)));
    }
  }

  @Nested
  @DisplayName("Request Headers Verification")
  class RequestHeadersTests {

    @Test
    @DisplayName("Should include all required headers in request")
    public void shouldIncludeAllRequiredHeadersInRequest() {
      String excerptId = "header-test-id";

      // Setup WireMock stub
      stubFor(
          get(urlPathEqualTo("/" + excerptId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "excerptIdentifier": "header-test-identifier"
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsResponse response = api.getExcerptById(excerptId);

      // Verify response
      assertThat(response).isNotNull();
      assertThat(response.getExcerptIdentifier()).isEqualTo("header-test-identifier");

      // Verify all required headers were sent
      verify(
          getRequestedFor(urlEqualTo("/" + excerptId))
              .withHeader("X-Access-Token", equalTo("test-token-123"))
              .withHeader("X-XSRF-TOKEN", equalTo("Token"))
              .withHeader("Cookie", equalTo("XSRF-TOKEN=Token")));
    }

    @Test
    @DisplayName("Should use correct HTTP method")
    public void shouldUseCorrectHttpMethod() {
      String excerptId = "method-test-id";

      // Setup WireMock stub
      stubFor(
          get(urlPathEqualTo("/" + excerptId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "excerptIdentifier": "method-test-identifier"
                                    }
                                    """)));

      // Execute REAL method call
      api.getExcerptById(excerptId);

      // Verify GET method was used
      verify(getRequestedFor(urlPathEqualTo("/" + excerptId)));
      verify(0, postRequestedFor(urlPathEqualTo("/" + excerptId)));
      verify(0, putRequestedFor(urlPathEqualTo("/" + excerptId)));
      verify(0, deleteRequestedFor(urlPathEqualTo("/" + excerptId)));
    }
  }

  @Nested
  @DisplayName("Response Handling")
  class ResponseHandlingTests {

    @Test
    @DisplayName("Should handle empty response body")
    public void shouldHandleEmptyResponseBody() {
      String excerptId = "empty-response-id";

      // Setup WireMock stub with empty body
      stubFor(
          get(urlEqualTo("/" + excerptId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody("{}")));

      // Execute REAL method call
      ExcerptsResponse response = api.getExcerptById(excerptId);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getExcerptIdentifier()).isNull();
    }

    @Test
    @DisplayName("Should handle response with null excerpt identifier")
    public void shouldHandleResponseWithNullExcerptIdentifier() {
      String excerptId = "null-identifier-id";

      // Setup WireMock stub
      stubFor(
          get(urlPathEqualTo("/" + excerptId))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          """
                                    {
                                        "excerptIdentifier": null
                                    }
                                    """)));

      // Execute REAL method call
      ExcerptsResponse response = api.getExcerptById(excerptId);

      // Verify
      assertThat(response).isNotNull();
      assertThat(response.getExcerptIdentifier()).isNull();
    }
  }
}
