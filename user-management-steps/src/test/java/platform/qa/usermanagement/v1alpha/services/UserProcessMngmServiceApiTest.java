/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.usermanagement.v1alpha.services;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.usermanagement.pojo.response.CreateApplicationResponse;
import platform.qa.usermanagement.pojo.response.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@DisplayName("UserProcessMngmServiceApi Integration Tests")
public class UserProcessMngmServiceApiTest {

    private static WireMockServer wireMockServer;
    private UserProcessMngmServiceApi api;
    private Service service;

    @BeforeAll
    public static void setupClass() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .port(8090)
                .bindAddress("localhost"));
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
        User user = User.builder().token("test-token-456").build();
        service = new Service("http://localhost:8090", user);

        // Create real API instance - NO MOCKS
        api = new UserProcessMngmServiceApi(service);
    }

    @Nested
    @DisplayName("Create Application - Success Scenarios")
    class CreateApplicationSuccessTests {

        @Test
        @DisplayName("Should create application with form payload")
        public void shouldCreateApplicationWithFormPayload() {
            String serviceId = "digital-service-123";
            Map<String, Object> formPayload = new HashMap<>();
            formPayload.put("name", "Test Application");
            formPayload.put("description", "Test Description");

            // Setup WireMock stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/services/" + serviceId + "/applications"))
                    .willReturn(aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "application": {
                                            "id": "app-001",
                                            "status": "CREATED"
                                        }
                                    }
                                    """)));

            // Execute REAL method call
            CreateApplicationResponse response = api.createApplication(serviceId, formPayload);

            // Verify
            assertThat(response).isNotNull();
            assertThat(response.getApplication()).isNotNull();
            assertThat(response.getApplication().getId()).isEqualTo("app-001");
            assertThat(response.getApplication().getStatus()).isEqualTo("CREATED");

            // Verify request was made correctly
            verify(postRequestedFor(urlEqualTo("/user-process-management/api/v1alpha/services/" + serviceId + "/applications"))
                    .withHeader("X-Access-Token", equalTo("test-token-456"))
                    .withHeader("X-XSRF-TOKEN", equalTo("Token"))
                    .withHeader("Cookie", equalTo("XSRF-TOKEN=Token")));
        }

        @Test
        @DisplayName("Should create application without form payload")
        public void shouldCreateApplicationWithoutFormPayload() {
            String serviceId = "digital-service-456";

            // Setup WireMock stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/services/" + serviceId + "/applications"))
                    .willReturn(aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "application": {
                                            "id": "app-002",
                                            "status": "CREATED"
                                        }
                                    }
                                    """)));

            // Execute REAL method call
            CreateApplicationResponse response = api.createApplication(serviceId);

            // Verify
            assertThat(response).isNotNull();
            assertThat(response.getApplication()).isNotNull();
            assertThat(response.getApplication().getId()).isEqualTo("app-002");
            assertThat(response.getApplication().getStatus()).isEqualTo("CREATED");
        }
    }

    @Nested
    @DisplayName("Create Application - Error Scenarios")
    class CreateApplicationErrorTests {

        @Test
        @DisplayName("Should handle 400 Bad Request")
        public void shouldHandle400BadRequest() {
            String serviceId = "digital-service-789";
            Map<String, Object> formPayload = new HashMap<>();
            formPayload.put("invalid", "data");
            int expectedStatusCode = 400;

            // Setup WireMock stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/services/" + serviceId + "/applications"))
                    .willReturn(aResponse()
                            .withStatus(expectedStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-400",
                                        "code": "BAD_REQUEST",
                                        "message": "Invalid application data"
                                    }
                                    """)));

            // Execute REAL method call
            ErrorResponse errorResponse = api.createApplication(serviceId, formPayload, expectedStatusCode);

            // Verify
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-400");
            assertThat(errorResponse.getCode()).isEqualTo("BAD_REQUEST");
            assertThat(errorResponse.getMessage()).isEqualTo("Invalid application data");
        }

        @Test
        @DisplayName("Should handle 404 Not Found - Service not found")
        public void shouldHandle404NotFound() {
            String serviceId = "non-existent-service";
            Map<String, Object> formPayload = new HashMap<>();
            int expectedStatusCode = 404;

            // Setup WireMock stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/services/" + serviceId + "/applications"))
                    .willReturn(aResponse()
                            .withStatus(expectedStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-404",
                                        "code": "NOT_FOUND",
                                        "message": "Service not found"
                                    }
                                    """)));

            // Execute REAL method call
            ErrorResponse errorResponse = api.createApplication(serviceId, formPayload, expectedStatusCode);

            // Verify
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-404");
            assertThat(errorResponse.getCode()).isEqualTo("NOT_FOUND");
            assertThat(errorResponse.getMessage()).isEqualTo("Service not found");
        }

        @Test
        @DisplayName("Should handle 401 Unauthorized")
        public void shouldHandle401Unauthorized() {
            String serviceId = "digital-service-unauthorized";
            Map<String, Object> formPayload = new HashMap<>();
            int expectedStatusCode = 401;

            // Setup WireMock stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/services/" + serviceId + "/applications"))
                    .willReturn(aResponse()
                            .withStatus(expectedStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-401",
                                        "code": "UNAUTHORIZED",
                                        "message": "Authentication required"
                                    }
                                    """)));

            // Execute REAL method call
            ErrorResponse errorResponse = api.createApplication(serviceId, formPayload, expectedStatusCode);

            // Verify
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-401");
            assertThat(errorResponse.getCode()).isEqualTo("UNAUTHORIZED");
            assertThat(errorResponse.getMessage()).isEqualTo("Authentication required");
        }
    }

    @Nested
    @DisplayName("Get Application - Success Scenarios")
    class GetApplicationSuccessTests {

        @Test
        @DisplayName("Should get application by ID")
        public void shouldGetApplicationById() {
            String applicationId = "app-get-123";

            // Setup WireMock stub
            stubFor(get(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "application": {
                                            "id": "app-get-123",
                                            "status": "IN_PROGRESS"
                                        }
                                    }
                                    """)));

            // Execute REAL method call
            CreateApplicationResponse response = api.getApplication(applicationId);

            // Verify
            assertThat(response).isNotNull();
            assertThat(response.getApplication()).isNotNull();
            assertThat(response.getApplication().getId()).isEqualTo("app-get-123");
            assertThat(response.getApplication().getStatus()).isEqualTo("IN_PROGRESS");

            // Verify request was made correctly
            verify(getRequestedFor(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId))
                    .withHeader("X-Access-Token", equalTo("test-token-456")));
        }
    }

    @Nested
    @DisplayName("Get Application - Error Scenarios")
    class GetApplicationErrorTests {

        @Test
        @DisplayName("Should handle 404 Not Found - Application not found")
        public void shouldHandle404ApplicationNotFound() {
            String applicationId = "non-existent-app";
            int expectedStatusCode = 404;

            // Setup WireMock stub
            stubFor(get(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId))
                    .willReturn(aResponse()
                            .withStatus(expectedStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-404-app",
                                        "code": "NOT_FOUND",
                                        "message": "Application not found"
                                    }
                                    """)));

            // Execute REAL method call
            ErrorResponse errorResponse = api.getApplication(applicationId, expectedStatusCode);

            // Verify
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-404-app");
            assertThat(errorResponse.getCode()).isEqualTo("NOT_FOUND");
            assertThat(errorResponse.getMessage()).isEqualTo("Application not found");
        }

        @Test
        @DisplayName("Should handle 403 Forbidden")
        public void shouldHandle403Forbidden() {
            String applicationId = "restricted-app";
            int expectedStatusCode = 403;

            // Setup WireMock stub
            stubFor(get(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId))
                    .willReturn(aResponse()
                            .withStatus(expectedStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-403",
                                        "code": "FORBIDDEN",
                                        "message": "Access denied to this application"
                                    }
                                    """)));

            // Execute REAL method call
            ErrorResponse errorResponse = api.getApplication(applicationId, expectedStatusCode);

            // Verify
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-403");
            assertThat(errorResponse.getCode()).isEqualTo("FORBIDDEN");
            assertThat(errorResponse.getMessage()).isEqualTo("Access denied to this application");
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationTests {

        @Test
        @DisplayName("Should create and retrieve application in sequence")
        public void shouldCreateAndRetrieveApplicationInSequence() {
            String serviceId = "integration-service";
            String applicationId = "app-integration-001";
            Map<String, Object> formPayload = new HashMap<>();
            formPayload.put("name", "Integration Test App");

            // Setup create stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/services/" + serviceId + "/applications"))
                    .willReturn(aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody(String.format("""
                                    {
                                        "application": {
                                            "id": "%s",
                                            "status": "CREATED"
                                        }
                                    }
                                    """, applicationId))));

            // Setup get stub
            stubFor(get(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(String.format("""
                                    {
                                        "application": {
                                            "id": "%s",
                                            "status": "CREATED"
                                        }
                                    }
                                    """, applicationId))));

            // Execute REAL create
            CreateApplicationResponse createResponse = api.createApplication(serviceId, formPayload);
            assertThat(createResponse.getApplication().getId()).isEqualTo(applicationId);

            // Execute REAL get using the created application ID
            CreateApplicationResponse getResponse = api.getApplication(createResponse.getApplication().getId());
            assertThat(getResponse.getApplication().getId()).isEqualTo(applicationId);
            assertThat(getResponse.getApplication().getStatus()).isEqualTo("CREATED");
        }
    }
}

