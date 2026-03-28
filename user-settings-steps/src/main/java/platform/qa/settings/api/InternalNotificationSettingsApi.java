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

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

import platform.qa.entities.Service;
import platform.qa.settings.api.spec.UserSettingsSpecification;
import platform.qa.settings.pojo.request.CreateNotificationSettingsInputDto;
import platform.qa.settings.pojo.response.CreateNotificationSettingsOutputDto;
import platform.qa.settings.pojo.response.DetailedErrorResponse;

/**
 * Internal Notification Settings API for managing notification settings and channels.
 */
public class InternalNotificationSettingsApi extends UserSettingsSpecification {

  public static final String INTERNAL_NOTIFICATION_SETTINGS_ENDPOINT =
      "/internalapi/settings/notifications";
  public static final String INTERNAL_NOTIFICATION_SETTINGS_BY_USER_ENDPOINT =
      "/internalapi/settings/notifications/{keycloakUserId}";
  public static final String KEYCLOAK_USER_ID = "keycloakUserId";

  public InternalNotificationSettingsApi(Service service) {
    super(service);
  }

  /**
   * Create notification settings and channels for a user.
   *
   * @param input The notification settings input containing keycloak user ID and channels
   * @return CreateNotificationSettingsOutputDto with created settings and channels
   */
  public CreateNotificationSettingsOutputDto createNotificationSettings(
      CreateNotificationSettingsInputDto input) {
    return given()
        .spec(requestSpec)
        .body(input)
        .post(INTERNAL_NOTIFICATION_SETTINGS_ENDPOINT)
        .then()
        .statusCode(SC_CREATED)
        .extract()
        .as(CreateNotificationSettingsOutputDto.class);
  }

  /**
   * Create notification settings and channels with expected status code.
   *
   * @param input The notification settings input containing keycloak user ID and channels
   * @param expectedStatusCode Expected HTTP status code
   * @return CreateNotificationSettingsOutputDto for success, null for error codes
   */
  public CreateNotificationSettingsOutputDto createNotificationSettings(
      CreateNotificationSettingsInputDto input, int expectedStatusCode) {
    if (expectedStatusCode == SC_CREATED) {
      return createNotificationSettings(input);
    }
    given()
        .spec(requestSpec)
        .body(input)
        .post(INTERNAL_NOTIFICATION_SETTINGS_ENDPOINT)
        .then()
        .statusCode(expectedStatusCode);
    return null;
  }

  /**
   * Create notification settings and expect error response.
   *
   * @param input The notification settings input containing keycloak user ID and channels
   * @param expectedStatusCode Expected HTTP status code for error
   * @return DetailedErrorResponse with error details
   */
  public DetailedErrorResponse createNotificationSettingsWithError(
      CreateNotificationSettingsInputDto input, int expectedStatusCode) {
    return given()
        .spec(requestSpec)
        .body(input)
        .post(INTERNAL_NOTIFICATION_SETTINGS_ENDPOINT)
        .then()
        .statusCode(expectedStatusCode)
        .extract()
        .as(DetailedErrorResponse.class);
  }

  /**
   * Delete notification settings and all associated channels for a user.
   *
   * @param keycloakUserId The Keycloak user ID
   */
  public void deleteNotificationSettings(String keycloakUserId) {
    given()
        .spec(requestSpec)
        .pathParam(KEYCLOAK_USER_ID, keycloakUserId)
        .delete(INTERNAL_NOTIFICATION_SETTINGS_BY_USER_ENDPOINT)
        .then()
        .statusCode(SC_NO_CONTENT);
  }

  /**
   * Delete notification settings with expected status code.
   *
   * @param keycloakUserId The Keycloak user ID
   * @param expectedStatusCode Expected HTTP status code
   */
  public void deleteNotificationSettings(String keycloakUserId, int expectedStatusCode) {
    given()
        .spec(requestSpec)
        .pathParam(KEYCLOAK_USER_ID, keycloakUserId)
        .delete(INTERNAL_NOTIFICATION_SETTINGS_BY_USER_ENDPOINT)
        .then()
        .statusCode(expectedStatusCode);
  }

  /**
   * Delete notification settings and expect error response.
   *
   * @param keycloakUserId The Keycloak user ID
   * @param expectedStatusCode Expected HTTP status code for error
   * @return DetailedErrorResponse with error details
   */
  public DetailedErrorResponse deleteNotificationSettingsWithError(
      String keycloakUserId, int expectedStatusCode) {
    return given()
        .spec(requestSpec)
        .pathParam(KEYCLOAK_USER_ID, keycloakUserId)
        .delete(INTERNAL_NOTIFICATION_SETTINGS_BY_USER_ENDPOINT)
        .then()
        .statusCode(expectedStatusCode)
        .extract()
        .as(DetailedErrorResponse.class);
  }
}
