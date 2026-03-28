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
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

import platform.qa.entities.Service;
import platform.qa.settings.api.spec.UserSettingsSpecification;
import platform.qa.settings.pojo.request.OtpData;

/** Internal OTP API for managing OTP (One-Time Password) data for testing purposes. */
public class InternalOtpApi extends UserSettingsSpecification {

  public static final String INTERNAL_OTP_ENDPOINT = "/internalapi/otp/{userId}/{channel}";
  public static final String USER_ID = "userId";
  public static final String CHANNEL = "channel";

  public InternalOtpApi(Service service) {
    super(service);
  }

  /** Retrieve OTP data for a specific user and channel */
  public OtpData getOtpData(String userId, String channel) {
    return given()
        .spec(requestSpec)
        .pathParam(USER_ID, userId)
        .pathParam(CHANNEL, channel)
        .get(INTERNAL_OTP_ENDPOINT)
        .then()
        .statusCode(SC_OK)
        .extract()
        .as(OtpData.class);
  }

  /** Create or update OTP data for a specific user and channel */
  public OtpData createOrUpdateOtpData(String userId, String channel, OtpData otpData) {
    return given()
        .spec(requestSpec)
        .pathParam(USER_ID, userId)
        .pathParam(CHANNEL, channel)
        .body(otpData)
        .put(INTERNAL_OTP_ENDPOINT)
        .then()
        .statusCode(SC_OK)
        .extract()
        .as(OtpData.class);
  }

  /** Delete OTP data for a specific user and channel */
  public void deleteOtpData(String userId, String channel) {
    given()
        .spec(requestSpec)
        .pathParam(USER_ID, userId)
        .pathParam(CHANNEL, channel)
        .delete(INTERNAL_OTP_ENDPOINT)
        .then()
        .statusCode(SC_NO_CONTENT);
  }
}
