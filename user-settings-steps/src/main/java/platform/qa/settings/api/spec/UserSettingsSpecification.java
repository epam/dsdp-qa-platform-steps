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
package platform.qa.settings.api.spec;

import static io.restassured.RestAssured.config;
import static io.restassured.config.LogConfig.logConfig;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import platform.qa.entities.Service;

public abstract class UserSettingsSpecification {

  protected static final String HEADER_USER_ACCESS_TOKEN_KEYCLOAK = "X-Access-Token";
  protected static final String HEADER_X_XSRF_TOKEN_NAME = "X-XSRF-TOKEN";
  protected static final String HEADER_X_XSRF_TOKEN_VALUE = "Token";
  protected static final String HEADER_COOKIE_NAME = "Cookie";
  protected static final String HEADER_COOKIE_VALUE = "XSRF-TOKEN=Token";
  protected final RequestSpecification requestSpec;

  protected UserSettingsSpecification(Service service) {
    String url = service.getUrl();
    requestSpec =
        new RequestSpecBuilder()
            .setConfig(config().logConfig(logConfig().enablePrettyPrinting(Boolean.TRUE)))
            .setContentType(ContentType.JSON)
            .setBaseUri(url)
            .addHeader(HEADER_USER_ACCESS_TOKEN_KEYCLOAK, service.getUser().getToken())
            .addHeader(HEADER_X_XSRF_TOKEN_NAME, HEADER_X_XSRF_TOKEN_VALUE)
            .addHeader(HEADER_COOKIE_NAME, HEADER_COOKIE_VALUE)
            .build();
  }
}
