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

package platform.qa.usermanagement.v1alpha.step;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.usermanagement.pojo.response.ApplicationResponse;
import platform.qa.usermanagement.pojo.response.StepResponse;
import platform.qa.usermanagement.pojo.response.SubmitStepResponse;
import platform.qa.usermanagement.pojo.response.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@DisplayName("UserProcessMngmStepApi Integration Tests")
public class UserProcessMngmStepApiTest {

    private static WireMockServer wireMockServer;
    private UserProcessMngmStepApi api;
    private Service service;

    @BeforeAll
    public static void setupClass() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .port(8091)
                .bindAddress("localhost"));
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
        User user = User.builder().token("test-token-789").build();
        service = new Service("http://localhost:8091", user);

        // Create real API instance - NO MOCKS
        api = new UserProcessMngmStepApi(service);
    }

    @Nested
    @DisplayName("Submit Step - Success Scenarios")
    class SubmitStepSuccessTests {

        @Test
        @DisplayName("Should submit step with form payload")
        public void shouldSubmitStepWithFormPayload() {
            String applicationId = "app-001";
            String stepId = "step-001";
            Map<String, Object> formPayload = new HashMap<>();
            formPayload.put("field1", "value1");
            formPayload.put("field2", "value2");

            // Setup WireMock stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId + "/submit"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "application": {
                                            "id": "app-001",
                                            "status": "IN_PROGRESS",
                                            "steps": [
                                                {
                                                    "id": "step-001",
                                                    "type": "form",
                                                    "status": "SUBMITTED"
                                                },
                                                {
                                                    "id": "step-002",
                                                    "type": "form",
                                                    "status": "PENDING"
                                                }
                                            ]
                                        }
                                    }
                                    """)));

            // Execute REAL method call
            SubmitStepResponse response = api.submitStep(applicationId, stepId, formPayload);

            // Verify
            assertThat(response).isNotNull();
            assertThat(response.getApplication()).isNotNull();
            assertThat(response.getApplication().getId()).isEqualTo("app-001");
            assertThat(response.getApplication().getStatus()).isEqualTo("IN_PROGRESS");
            assertThat(response.getApplication().getSteps()).isNotEmpty();
            assertThat(response.getApplication().getSteps().get(0).getId()).isEqualTo("step-001");
            assertThat(response.getApplication().getSteps().get(0).getStatus()).isEqualTo("SUBMITTED");

            // Verify request was made correctly
            verify(postRequestedFor(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId + "/submit"))
                    .withHeader("X-Access-Token", equalTo("test-token-789"))
                    .withHeader("X-XSRF-TOKEN", equalTo("Token"))
                    .withHeader("Cookie", equalTo("XSRF-TOKEN=Token")));
        }

        @Test
        @DisplayName("Should submit final step without next step")
        public void shouldSubmitFinalStep() {
            String applicationId = "app-002";
            String stepId = "step-final";
            Map<String, Object> formPayload = new HashMap<>();
            formPayload.put("finalData", "complete");

            // Setup WireMock stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId + "/submit"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "application": {
                                            "id": "app-002",
                                            "status": "COMPLETED",
                                            "steps": [
                                                {
                                                    "id": "step-final",
                                                    "type": "form",
                                                    "status": "COMPLETED"
                                                }
                                            ]
                                        }
                                    }
                                    """)));

            // Execute REAL method call
            SubmitStepResponse response = api.submitStep(applicationId, stepId, formPayload);

            // Verify
            assertThat(response).isNotNull();
            assertThat(response.getApplication()).isNotNull();
            assertThat(response.getApplication().getId()).isEqualTo("app-002");
            assertThat(response.getApplication().getStatus()).isEqualTo("COMPLETED");
            assertThat(response.getApplication().getSteps().get(0).getId()).isEqualTo("step-final");
            assertThat(response.getApplication().getSteps().get(0).getStatus()).isEqualTo("COMPLETED");
        }
    }

    @Nested
    @DisplayName("Submit Step - Error Scenarios")
    class SubmitStepErrorTests {

        @Test
        @DisplayName("Should handle 400 Bad Request - Invalid form data")
        public void shouldHandle400BadRequest() {
            String applicationId = "app-003";
            String stepId = "step-003";
            Map<String, Object> formPayload = new HashMap<>();
            formPayload.put("invalid", "data");
            int expectedStatusCode = 400;

            // Setup WireMock stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId + "/submit"))
                    .willReturn(aResponse()
                            .withStatus(expectedStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-400",
                                        "code": "BAD_REQUEST",
                                        "message": "Invalid form data"
                                    }
                                    """)));

            // Execute REAL method call
            ErrorResponse errorResponse = api.submitStep(applicationId, stepId, formPayload, expectedStatusCode);

            // Verify
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-400");
            assertThat(errorResponse.getCode()).isEqualTo("BAD_REQUEST");
            assertThat(errorResponse.getMessage()).isEqualTo("Invalid form data");
        }

        @Test
        @DisplayName("Should handle 404 Not Found - Step not found")
        public void shouldHandle404StepNotFound() {
            String applicationId = "app-004";
            String stepId = "non-existent-step";
            Map<String, Object> formPayload = new HashMap<>();
            int expectedStatusCode = 404;

            // Setup WireMock stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId + "/submit"))
                    .willReturn(aResponse()
                            .withStatus(expectedStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-404",
                                        "code": "NOT_FOUND",
                                        "message": "Step not found"
                                    }
                                    """)));

            // Execute REAL method call
            ErrorResponse errorResponse = api.submitStep(applicationId, stepId, formPayload, expectedStatusCode);

            // Verify
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-404");
            assertThat(errorResponse.getCode()).isEqualTo("NOT_FOUND");
            assertThat(errorResponse.getMessage()).isEqualTo("Step not found");
        }

        @Test
        @DisplayName("Should handle 422 Unprocessable Entity - Validation failed")
        public void shouldHandle422ValidationFailed() {
            String applicationId = "app-005";
            String stepId = "step-005";
            Map<String, Object> formPayload = new HashMap<>();
            formPayload.put("email", "invalid-email");
            int expectedStatusCode = 422;

            // Setup WireMock stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId + "/submit"))
                    .willReturn(aResponse()
                            .withStatus(expectedStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-422",
                                        "code": "VALIDATION_ERROR",
                                        "message": "Email format is invalid"
                                    }
                                    """)));

            // Execute REAL method call
            ErrorResponse errorResponse = api.submitStep(applicationId, stepId, formPayload, expectedStatusCode);

            // Verify
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-422");
            assertThat(errorResponse.getCode()).isEqualTo("VALIDATION_ERROR");
            assertThat(errorResponse.getMessage()).isEqualTo("Email format is invalid");
        }
    }

    @Nested
    @DisplayName("Get Step - Success Scenarios")
    class GetStepSuccessTests {

        @Test
        @DisplayName("Should get step details")
        public void shouldGetStepDetails() {
            String applicationId = "app-get-001";
            String stepId = "step-get-001";

            // Setup WireMock stub
            stubFor(get(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "id": "step-get-001",
                                        "type": "personal-info",
                                        "status": "PENDING"
                                    }
                                    """)));

            // Execute REAL method call
            StepResponse response = api.getStep(applicationId, stepId);

            // Verify
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("step-get-001");
            assertThat(response.getType()).isEqualTo("personal-info");
            assertThat(response.getStatus()).isEqualTo("PENDING");

            // Verify request was made correctly
            verify(getRequestedFor(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId))
                    .withHeader("X-Access-Token", equalTo("test-token-789")));
        }

        @Test
        @DisplayName("Should get completed step details")
        public void shouldGetCompletedStepDetails() {
            String applicationId = "app-get-002";
            String stepId = "step-get-002";

            // Setup WireMock stub
            stubFor(get(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "id": "step-get-002",
                                        "type": "document-upload",
                                        "status": "COMPLETED",
                                        "form": {
                                            "data": {
                                                "documentId": "doc-123"
                                            }
                                        }
                                    }
                                    """)));

            // Execute REAL method call
            StepResponse response = api.getStep(applicationId, stepId);

            // Verify
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("step-get-002");
            assertThat(response.getType()).isEqualTo("document-upload");
            assertThat(response.getStatus()).isEqualTo("COMPLETED");
        }
    }

    @Nested
    @DisplayName("Get Step - Error Scenarios")
    class GetStepErrorTests {

        @Test
        @DisplayName("Should handle 404 Not Found - Step not found")
        public void shouldHandle404StepNotFound() {
            String applicationId = "app-get-404";
            String stepId = "non-existent-step";
            int expectedStatusCode = 404;

            // Setup WireMock stub
            stubFor(get(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId))
                    .willReturn(aResponse()
                            .withStatus(expectedStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-404-get",
                                        "code": "NOT_FOUND",
                                        "message": "Step not found"
                                    }
                                    """)));

            // Execute REAL method call
            ErrorResponse errorResponse = api.getStep(applicationId, stepId, expectedStatusCode);

            // Verify
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-404-get");
            assertThat(errorResponse.getCode()).isEqualTo("NOT_FOUND");
            assertThat(errorResponse.getMessage()).isEqualTo("Step not found");
        }

        @Test
        @DisplayName("Should handle 403 Forbidden - Access denied")
        public void shouldHandle403Forbidden() {
            String applicationId = "app-get-403";
            String stepId = "restricted-step";
            int expectedStatusCode = 403;

            // Setup WireMock stub
            stubFor(get(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId))
                    .willReturn(aResponse()
                            .withStatus(expectedStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-403-get",
                                        "code": "FORBIDDEN",
                                        "message": "Access denied to this step"
                                    }
                                    """)));

            // Execute REAL method call
            ErrorResponse errorResponse = api.getStep(applicationId, stepId, expectedStatusCode);

            // Verify
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-403-get");
            assertThat(errorResponse.getCode()).isEqualTo("FORBIDDEN");
            assertThat(errorResponse.getMessage()).isEqualTo("Access denied to this step");
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationTests {

        @Test
        @DisplayName("Should get step, submit it, and verify status change")
        public void shouldGetSubmitAndVerifyStep() {
            String applicationId = "app-integration";
            String stepId = "step-integration";
            Map<String, Object> formPayload = new HashMap<>();
            formPayload.put("answer", "Integration test answer");

            // Setup get step stub - before submission
            stubFor(get(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId))
                    .inScenario("Step Workflow")
                    .whenScenarioStateIs("Started")
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "id": "step-integration",
                                        "type": "form",
                                        "status": "PENDING"
                                    }
                                    """))
                    .willSetStateTo("Step Retrieved"));

            // Setup submit step stub
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + stepId + "/submit"))
                    .inScenario("Step Workflow")
                    .whenScenarioStateIs("Step Retrieved")
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "application": {
                                            "id": "app-integration",
                                            "status": "IN_PROGRESS",
                                            "steps": [
                                                {
                                                    "id": "step-integration",
                                                    "type": "form",
                                                    "status": "SUBMITTED"
                                                },
                                                {
                                                    "id": "step-next",
                                                    "type": "form",
                                                    "status": "PENDING"
                                                }
                                            ]
                                        }
                                    }
                                    """))
                    .willSetStateTo("Step Submitted"));

            // Execute REAL get step (before submission)
            StepResponse getResponse = api.getStep(applicationId, stepId);
            assertThat(getResponse.getStatus()).isEqualTo("PENDING");

            // Execute REAL submit step
            SubmitStepResponse submitResponse = api.submitStep(applicationId, stepId, formPayload);
            assertThat(submitResponse.getApplication().getStatus()).isEqualTo("IN_PROGRESS");
            assertThat(submitResponse.getApplication().getSteps().get(1).getId()).isEqualTo("step-next");
            assertThat(submitResponse.getApplication().getSteps().get(1).getStatus()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("Should handle multi-step workflow")
        public void shouldHandleMultiStepWorkflow() {
            String applicationId = "app-multi-step";
            String step1Id = "step-1";
            String step2Id = "step-2";
            Map<String, Object> formPayload1 = new HashMap<>();
            formPayload1.put("step1Data", "value1");
            Map<String, Object> formPayload2 = new HashMap<>();
            formPayload2.put("step2Data", "value2");

            // Setup step 1 submit
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + step1Id + "/submit"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(String.format("""
                                    {
                                        "application": {
                                            "id": "%s",
                                            "status": "IN_PROGRESS",
                                            "steps": [
                                                {
                                                    "id": "%s",
                                                    "type": "form",
                                                    "status": "SUBMITTED"
                                                },
                                                {
                                                    "id": "%s",
                                                    "type": "form",
                                                    "status": "PENDING"
                                                }
                                            ]
                                        }
                                    }
                                    """, applicationId, step1Id, step2Id))));

            // Setup step 2 submit
            stubFor(post(urlEqualTo("/user-process-management/api/v1alpha/applications/" + applicationId + "/steps/" + step2Id + "/submit"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(String.format("""
                                    {
                                        "application": {
                                            "id": "%s",
                                            "status": "COMPLETED",
                                            "steps": [
                                                {
                                                    "id": "%s",
                                                    "type": "form",
                                                    "status": "SUBMITTED"
                                                },
                                                {
                                                    "id": "%s",
                                                    "type": "form",
                                                    "status": "COMPLETED"
                                                }
                                            ]
                                        }
                                    }
                                    """, applicationId, step1Id, step2Id))));

            // Execute REAL step 1 submission
            SubmitStepResponse step1Response = api.submitStep(applicationId, step1Id, formPayload1);
            // Get the next step ID from the response
            String nextStepId = step1Response.getApplication().getSteps().stream()
                    .filter(step -> "PENDING".equals(step.getStatus()))
                    .findFirst()
                    .map(ApplicationResponse.StepInfo::getId)
                    .orElse(null);
            assertThat(nextStepId).isEqualTo(step2Id);

            // Execute REAL step 2 submission using the nextStepId from step 1
            SubmitStepResponse step2Response = api.submitStep(applicationId, nextStepId, formPayload2);
            assertThat(step2Response.getApplication().getStatus()).isEqualTo("COMPLETED");
            boolean hasMorePendingSteps = step2Response.getApplication().getSteps().stream()
                    .anyMatch(step -> "PENDING".equals(step.getStatus()));
            assertThat(hasMorePendingSteps).isFalse();
        }
    }
}

