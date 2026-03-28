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
package platform.qa.settings.api;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import platform.qa.entities.Service;
import platform.qa.settings.api.spec.UserSettingsSpecification;

public class UserSettingsApi extends UserSettingsSpecification {
  public static final String SETTINGS = "/api/settings/";

  public UserSettingsApi(Service service) {
    super(service);
  }

  public Response getAllCurrentUserSettings() {
    return given().spec(requestSpec).basePath(SETTINGS + "me").get();
  }

  public Response getSettingsByUserId(String keycloakUserId) {
    return given()
        .spec(requestSpec)
        .basePath(SETTINGS + "{userId}")
        .pathParam("userId", keycloakUserId)
        .get();
  }
}
