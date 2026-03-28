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
import platform.qa.entities.IEntity;
import platform.qa.entities.Service;
import platform.qa.settings.api.spec.UserSettingsSpecification;
import platform.qa.settings.enumerations.ChannelType;
import platform.qa.settings.pojo.request.Address;
import platform.qa.settings.pojo.request.DeactivationReason;
import platform.qa.settings.pojo.request.OtpData;

public class UserSettingsChannelsApi extends UserSettingsSpecification {

  public static final String ACTIVATE_PATH = "/api/settings/me/channels/%s/activate";
  public static final String DEACTIVATE_PATH = "/api/settings/me/channels/%s/deactivate";
  public static final String VALIDATE_PATH = "/api/settings/me/channels/email/validate";
  public static final String VERIFY_PATH = "/api/settings/me/channels/%s/verify";

  public UserSettingsChannelsApi(Service service) {
    super(service);
  }

  public <T extends IEntity> Response activate(ChannelType channelType, T channelEntity) {
    return given()
        .spec(requestSpec)
        .basePath(String.format(ACTIVATE_PATH, channelType.getType()))
        .body(channelEntity)
        .post();
  }

  public Response activate(ChannelType channelType, OtpData otpData) {
    return given()
        .spec(requestSpec)
        .basePath(String.format(ACTIVATE_PATH, channelType.getType()))
        .body(otpData)
        .post();
  }

  public Response activate(ChannelType channelType) {
    return given()
        .spec(requestSpec)
        .basePath(String.format(ACTIVATE_PATH, channelType.getType()))
        .post();
  }

  public Response deactivate(DeactivationReason deactivationReason, ChannelType channelType) {
    return given()
        .spec(requestSpec)
        .basePath(String.format(DEACTIVATE_PATH, channelType.getType()))
        .body(deactivationReason)
        .post();
  }

  public <T extends IEntity> Response validate(ChannelType channelType, T channelEntity) {
    return given()
        .spec(requestSpec)
        .basePath(String.format(VALIDATE_PATH, channelType.getType()))
        .body(channelEntity)
        .post();
  }

  public Response validate(Address address) {
    return given().spec(requestSpec).basePath(VALIDATE_PATH).body(address).post();
  }

  public <T extends IEntity> Response verify(ChannelType channelType, T channelEntity) {
    return given()
        .spec(requestSpec)
        .basePath(String.format(VERIFY_PATH, channelType.getType()))
        .body(channelEntity)
        .post();
  }

  public Response verify(ChannelType channelType, Address address) {
    return given()
        .spec(requestSpec)
        .basePath(String.format(VERIFY_PATH, channelType.getType()))
        .body(address)
        .post();
  }
}
