/*
 * Copyright 2026 EPAM Systems.
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

package platform.qa.settings.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.settings.pojo.request.ChannelInputDto;
import platform.qa.settings.pojo.request.CreateNotificationSettingsInputDto;
import platform.qa.settings.pojo.response.CreateNotificationSettingsOutputDto;
import platform.qa.settings.pojo.response.DetailedErrorResponse;

import java.util.Arrays;
import java.util.Collections;

@DisplayName("InternalNotificationSettingsApi Integration Tests")
class InternalNotificationSettingsApiTest {

    private static WireMockServer wireMockServer;
    private InternalNotificationSettingsApi api;
    private Service service;

    @BeforeAll
    static void setupClass() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .port(8092)
                .bindAddress("localhost"));
        wireMockServer.start();
        configureFor("localhost", 8092);
    }

    @AfterAll
    static void tearDownClass() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setup() {
        wireMockServer.resetAll();
        configureFor("localhost", 8092);

        User user = User.builder().token("test-token-settings").build();
        service = new Service("http://localhost:8092", user);
        api = new InternalNotificationSettingsApi(service);
    }

    @Nested
    @DisplayName("Create Notification Settings - Success Scenarios")
    class CreateNotificationSettingsSuccessTests {

        @Test
        @DisplayName("Should create notification settings with single email channel")
        void shouldCreateNotificationSettingsWithSingleChannel() {
            String keycloakUserId = "123e4567-e89b-12d3-a456-426614174000";
            String settingsId = "789e4567-e89b-12d3-a456-426614174999";
            String channelId = "456e4567-e89b-12d3-a456-426614174111";

            ChannelInputDto channel = ChannelInputDto.builder()
                    .channel("email")
                    .address("user@example.com")
                    .isActivated(true)
                    .build();

            CreateNotificationSettingsInputDto input = CreateNotificationSettingsInputDto.builder()
                    .keycloakUserId(keycloakUserId)
                    .channels(Collections.singletonList(channel))
                    .build();

            stubFor(post(urlEqualTo("/internalapi/settings/notifications"))
                    .willReturn(aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody(String.format("""
                                    {
                                        "settingsId": "%s",
                                        "channels": [
                                            {
                                                "id": "%s",
                                                "channel": "email",
                                                "address": "user@example.com",
                                                "isActivated": true,
                                                "createdAt": "2026-02-02T10:30:00"
                                            }
                                        ],
                                        "createdAt": "2026-02-02T10:30:00"
                                    }
                                    """, settingsId, channelId))));

            CreateNotificationSettingsOutputDto response = api.createNotificationSettings(input);

            assertThat(response).isNotNull();
            assertThat(response.getSettingsId()).isEqualTo(settingsId);
            assertThat(response.getChannels()).hasSize(1);
            assertThat(response.getChannels().get(0).getId()).isEqualTo(channelId);
            assertThat(response.getChannels().get(0).getChannel()).isEqualTo("email");
            assertThat(response.getChannels().get(0).getAddress()).isEqualTo("user@example.com");
            assertThat(response.getChannels().get(0).getIsActivated()).isTrue();
            assertThat(response.getCreatedAt()).isEqualTo("2026-02-02T10:30:00");

            verify(postRequestedFor(urlEqualTo("/internalapi/settings/notifications"))
                    .withHeader("X-Access-Token", equalTo("test-token-settings"))
                    .withHeader("X-XSRF-TOKEN", equalTo("Token"))
                    .withHeader("Cookie", equalTo("XSRF-TOKEN=Token")));
        }

        @Test
        @DisplayName("Should create notification settings with multiple channels")
        void shouldCreateNotificationSettingsWithMultipleChannels() {
            String keycloakUserId = "999e4567-e89b-12d3-a456-426614174000";

            ChannelInputDto emailChannel = ChannelInputDto.builder()
                    .channel("email")
                    .address("primary@example.com")
                    .isActivated(true)
                    .build();

            ChannelInputDto secondaryEmailChannel = ChannelInputDto.builder()
                    .channel("email")
                    .address("secondary@example.com")
                    .isActivated(false)
                    .build();

            CreateNotificationSettingsInputDto input = CreateNotificationSettingsInputDto.builder()
                    .keycloakUserId(keycloakUserId)
                    .channels(Arrays.asList(emailChannel, secondaryEmailChannel))
                    .build();

            stubFor(post(urlEqualTo("/internalapi/settings/notifications"))
                    .willReturn(aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "settingsId": "multi-settings-id",
                                        "channels": [
                                            {
                                                "id": "channel-1",
                                                "channel": "email",
                                                "address": "primary@example.com",
                                                "isActivated": true,
                                                "createdAt": "2026-02-02T10:30:00"
                                            },
                                            {
                                                "id": "channel-2",
                                                "channel": "email",
                                                "address": "secondary@example.com",
                                                "isActivated": false,
                                                "createdAt": "2026-02-02T10:30:01"
                                            }
                                        ],
                                        "createdAt": "2026-02-02T10:30:00"
                                    }
                                    """)));

            CreateNotificationSettingsOutputDto response = api.createNotificationSettings(input);

            assertThat(response).isNotNull();
            assertThat(response.getSettingsId()).isEqualTo("multi-settings-id");
            assertThat(response.getChannels()).hasSize(2);
            assertThat(response.getChannels().get(0).getAddress()).isEqualTo("primary@example.com");
            assertThat(response.getChannels().get(0).getIsActivated()).isTrue();
            assertThat(response.getChannels().get(1).getAddress()).isEqualTo("secondary@example.com");
            assertThat(response.getChannels().get(1).getIsActivated()).isFalse();
        }
    }

    @Nested
    @DisplayName("Create Notification Settings - Error Scenarios")
    class CreateNotificationSettingsErrorTests {

        @Test
        @DisplayName("Should handle 400 Bad Request - Invalid email format")
        void shouldHandle400BadRequest() {
            String keycloakUserId = "bad-user-id";
            ChannelInputDto channel = ChannelInputDto.builder()
                    .channel("email")
                    .address("invalid-email")
                    .isActivated(true)
                    .build();

            CreateNotificationSettingsInputDto input = CreateNotificationSettingsInputDto.builder()
                    .keycloakUserId(keycloakUserId)
                    .channels(Collections.singletonList(channel))
                    .build();

            stubFor(post(urlEqualTo("/internalapi/settings/notifications"))
                    .willReturn(aResponse()
                            .withStatus(400)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-400",
                                        "code": "VALIDATION_ERROR",
                                        "message": "Invalid email format"
                                    }
                                    """)));

            DetailedErrorResponse errorResponse = api.createNotificationSettingsWithError(input, 400);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-400");
            assertThat(errorResponse.getCode()).isEqualTo("VALIDATION_ERROR");
            assertThat(errorResponse.getMessage()).isEqualTo("Invalid email format");
        }

        @Test
        @DisplayName("Should handle 401 Unauthorized")
        void shouldHandle401Unauthorized() {
            CreateNotificationSettingsInputDto input = CreateNotificationSettingsInputDto.builder()
                    .keycloakUserId("user-401")
                    .channels(Collections.emptyList())
                    .build();

            stubFor(post(urlEqualTo("/internalapi/settings/notifications"))
                    .willReturn(aResponse()
                            .withStatus(401)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-401",
                                        "code": "UNAUTHORIZED",
                                        "message": "Authentication required"
                                    }
                                    """)));

            DetailedErrorResponse errorResponse = api.createNotificationSettingsWithError(input, 401);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-401");
            assertThat(errorResponse.getCode()).isEqualTo("UNAUTHORIZED");
        }

        @Test
        @DisplayName("Should handle 500 Internal Server Error")
        void shouldHandle500InternalServerError() {
            CreateNotificationSettingsInputDto input = CreateNotificationSettingsInputDto.builder()
                    .keycloakUserId("user-500")
                    .channels(Collections.emptyList())
                    .build();

            stubFor(post(urlEqualTo("/internalapi/settings/notifications"))
                    .willReturn(aResponse()
                            .withStatus(500)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-500",
                                        "code": "INTERNAL_ERROR",
                                        "message": "Database connection failed"
                                    }
                                    """)));

            DetailedErrorResponse errorResponse = api.createNotificationSettingsWithError(input, 500);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-500");
            assertThat(errorResponse.getCode()).isEqualTo("INTERNAL_ERROR");
            assertThat(errorResponse.getMessage()).isEqualTo("Database connection failed");
        }
    }

    @Nested
    @DisplayName("Delete Notification Settings - Success Scenarios")
    class DeleteNotificationSettingsSuccessTests {

        @Test
        @DisplayName("Should delete notification settings successfully")
        void shouldDeleteNotificationSettingsSuccessfully() {
            String keycloakUserId = "123e4567-e89b-12d3-a456-426614174000";

            stubFor(delete(urlEqualTo("/internalapi/settings/notifications/" + keycloakUserId))
                    .willReturn(aResponse()
                            .withStatus(204)));

            api.deleteNotificationSettings(keycloakUserId);

            verify(deleteRequestedFor(urlEqualTo("/internalapi/settings/notifications/" + keycloakUserId))
                    .withHeader("X-Access-Token", equalTo("test-token-settings")));
        }

        @Test
        @DisplayName("Should delete notification settings with UUID")
        void shouldDeleteNotificationSettingsWithUUID() {
            String keycloakUserId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";

            stubFor(delete(urlEqualTo("/internalapi/settings/notifications/" + keycloakUserId))
                    .willReturn(aResponse()
                            .withStatus(204)));

            api.deleteNotificationSettings(keycloakUserId);

            verify(deleteRequestedFor(urlEqualTo("/internalapi/settings/notifications/" + keycloakUserId)));
        }
    }

    @Nested
    @DisplayName("Delete Notification Settings - Error Scenarios")
    class DeleteNotificationSettingsErrorTests {

        @Test
        @DisplayName("Should handle 400 Bad Request - Invalid UUID format")
        void shouldHandle400InvalidUUID() {
            String keycloakUserId = "invalid-uuid-format";

            stubFor(delete(urlEqualTo("/internalapi/settings/notifications/" + keycloakUserId))
                    .willReturn(aResponse()
                            .withStatus(400)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-400-delete",
                                        "code": "BAD_REQUEST",
                                        "message": "Invalid keycloak user ID format"
                                    }
                                    """)));

            DetailedErrorResponse errorResponse = api.deleteNotificationSettingsWithError(keycloakUserId, 400);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-400-delete");
            assertThat(errorResponse.getCode()).isEqualTo("BAD_REQUEST");
            assertThat(errorResponse.getMessage()).isEqualTo("Invalid keycloak user ID format");
        }

        @Test
        @DisplayName("Should handle 401 Unauthorized")
        void shouldHandle401Unauthorized() {
            String keycloakUserId = "unauthorized-user";

            stubFor(delete(urlEqualTo("/internalapi/settings/notifications/" + keycloakUserId))
                    .willReturn(aResponse()
                            .withStatus(401)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-401-delete",
                                        "code": "UNAUTHORIZED",
                                        "message": "Authentication required"
                                    }
                                    """)));

            DetailedErrorResponse errorResponse = api.deleteNotificationSettingsWithError(keycloakUserId, 401);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getCode()).isEqualTo("UNAUTHORIZED");
        }

        @Test
        @DisplayName("Should handle 404 Not Found - Settings not found")
        void shouldHandle404NotFound() {
            String keycloakUserId = "non-existent-user";

            stubFor(delete(urlEqualTo("/internalapi/settings/notifications/" + keycloakUserId))
                    .willReturn(aResponse()
                            .withStatus(404)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-404-delete",
                                        "code": "NOT_FOUND",
                                        "message": "Settings not found for the given keycloak user ID"
                                    }
                                    """)));

            DetailedErrorResponse errorResponse = api.deleteNotificationSettingsWithError(keycloakUserId, 404);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-404-delete");
            assertThat(errorResponse.getCode()).isEqualTo("NOT_FOUND");
            assertThat(errorResponse.getMessage()).isEqualTo("Settings not found for the given keycloak user ID");
        }

        @Test
        @DisplayName("Should handle 500 Internal Server Error")
        void shouldHandle500InternalServerError() {
            String keycloakUserId = "error-user";

            stubFor(delete(urlEqualTo("/internalapi/settings/notifications/" + keycloakUserId))
                    .willReturn(aResponse()
                            .withStatus(500)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "traceId": "trace-500-delete",
                                        "code": "INTERNAL_ERROR",
                                        "message": "Failed to delete settings"
                                    }
                                    """)));

            DetailedErrorResponse errorResponse = api.deleteNotificationSettingsWithError(keycloakUserId, 500);

            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.getTraceId()).isEqualTo("trace-500-delete");
            assertThat(errorResponse.getMessage()).isEqualTo("Failed to delete settings");
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationTests {

        @Test
        @DisplayName("Should create and delete notification settings in sequence")
        void shouldCreateAndDeleteNotificationSettingsInSequence() {
            String keycloakUserId = "integration-user-001";
            String settingsId = "integration-settings-id";

            ChannelInputDto channel = ChannelInputDto.builder()
                    .channel("email")
                    .address("integration@example.com")
                    .isActivated(true)
                    .build();

            CreateNotificationSettingsInputDto input = CreateNotificationSettingsInputDto.builder()
                    .keycloakUserId(keycloakUserId)
                    .channels(Collections.singletonList(channel))
                    .build();

            // Setup create stub
            stubFor(post(urlEqualTo("/internalapi/settings/notifications"))
                    .willReturn(aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody(String.format("""
                                    {
                                        "settingsId": "%s",
                                        "channels": [
                                            {
                                                "id": "channel-integration-001",
                                                "channel": "email",
                                                "address": "integration@example.com",
                                                "isActivated": true,
                                                "createdAt": "2026-02-02T10:30:00"
                                            }
                                        ],
                                        "createdAt": "2026-02-02T10:30:00"
                                    }
                                    """, settingsId))));

            // Setup delete stub
            stubFor(delete(urlEqualTo("/internalapi/settings/notifications/" + keycloakUserId))
                    .willReturn(aResponse()
                            .withStatus(204)));

            // Execute create
            CreateNotificationSettingsOutputDto createResponse = api.createNotificationSettings(input);
            assertThat(createResponse.getSettingsId()).isEqualTo(settingsId);

            // Execute delete
            api.deleteNotificationSettings(keycloakUserId);

            // Verify both operations
            verify(postRequestedFor(urlEqualTo("/internalapi/settings/notifications")));
            verify(deleteRequestedFor(urlEqualTo("/internalapi/settings/notifications/" + keycloakUserId)));
        }

        @Test
        @DisplayName("Should handle workflow with multiple channels")
        void shouldHandleWorkflowWithMultipleChannels() {
            String keycloakUserId = "workflow-user";

            ChannelInputDto primaryChannel = ChannelInputDto.builder()
                    .channel("email")
                    .address("primary@workflow.com")
                    .isActivated(true)
                    .build();

            ChannelInputDto secondaryChannel = ChannelInputDto.builder()
                    .channel("email")
                    .address("secondary@workflow.com")
                    .isActivated(false)
                    .build();

            CreateNotificationSettingsInputDto input = CreateNotificationSettingsInputDto.builder()
                    .keycloakUserId(keycloakUserId)
                    .channels(Arrays.asList(primaryChannel, secondaryChannel))
                    .build();

            stubFor(post(urlEqualTo("/internalapi/settings/notifications"))
                    .willReturn(aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                        "settingsId": "workflow-settings",
                                        "channels": [
                                            {
                                                "id": "ch-1",
                                                "channel": "email",
                                                "address": "primary@workflow.com",
                                                "isActivated": true,
                                                "createdAt": "2026-02-02T10:30:00"
                                            },
                                            {
                                                "id": "ch-2",
                                                "channel": "email",
                                                "address": "secondary@workflow.com",
                                                "isActivated": false,
                                                "createdAt": "2026-02-02T10:30:01"
                                            }
                                        ],
                                        "createdAt": "2026-02-02T10:30:00"
                                    }
                                    """)));

            CreateNotificationSettingsOutputDto response = api.createNotificationSettings(input);

            assertThat(response.getChannels()).hasSize(2);
            assertThat(response.getChannels().get(0).getIsActivated()).isTrue();
            assertThat(response.getChannels().get(1).getIsActivated()).isFalse();
        }
    }
}
