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

package platform.qa.api;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static platform.qa.constants.Constants.COOKIE_HEADER_NAME;
import static platform.qa.constants.Constants.COOKIE_HEADER_VALUE;
import static platform.qa.constants.Constants.XSRF_HEADER_NAME;
import static platform.qa.constants.Constants.XSRF_HEADER_VALUE;
import static platform.qa.constants.Constants.X_ACCESS_TOKEN_HEADER;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.entities.auth.AuthorizationRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.http.HttpStatus;

/**
 * Implement abilities to manipulate camunda authorizations
 */
@Log4j2
public class AuthorizationApi {
    private final RequestSpecification requestSpec;

    public AuthorizationApi(Service bpms) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setConfig(
                        config()
                                .logConfig(logConfig()
                                        .enableLoggingOfRequestAndResponseIfValidationFails()
                                        .enablePrettyPrinting(Boolean.TRUE)))
                .setBaseUri(bpms.getUrl())
                .setContentType(ContentType.JSON)
                .addHeader(X_ACCESS_TOKEN_HEADER, bpms.getUser().getToken())
                .addHeader(XSRF_HEADER_NAME, XSRF_HEADER_VALUE)
                .addHeader(COOKIE_HEADER_NAME, COOKIE_HEADER_VALUE);

        this.requestSpec = requestSpecBuilder.build();
    }

    public String createAuthorization(AuthorizationRequest authorizationRequest) {
        log.info("Creating authorization request");
        String authorizationId = given()
                .spec(requestSpec)
                .body(authorizationRequest, ObjectMapperType.JACKSON_2)
                .when()
                .post("api/authorization/create")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .get("id")
                .toString();

        log.info("Created authorization id: {}", authorizationId);
        return authorizationId;
    }

    public List<Map> getAuthorizations() {
        log.info("Get all authorizations");
        return given()
                .spec(requestSpec)
                .get("api/authorization")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract().response()
                .jsonPath()
                .getList("", Map.class);
    }

    public List<Map> getAuthorizationsByParam(String parameterName, String parameterValue) {
        log.info("Get all authorizations by parameterName: {} and parameterValue: {}", parameterName, parameterValue);
        return given()
                .spec(requestSpec)
                .queryParam(parameterName, parameterValue)
                .get("api/authorization")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract().response()
                .jsonPath()
                .getList("", Map.class);
    }

    public List<Map> getAuthorizationsByParams(Map<String, ?> params) {
        log.info("Get all authorizations by parameters: {}", params);
        return given()
                .spec(requestSpec)
                .queryParams(params)
                .get("api/authorization")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract().response()
                .jsonPath()
                .getList("", Map.class);
    }

    public List<Map> getProcessDefinitionAuth(String groupId, String processKey, Integer resourceType) {
        log.info("Get authorizations for groupId: {}, resourceType: {} and processKey: {}", groupId, resourceType,
                processKey);
        return getAuthorizationsByParams(
                Map.of("groupIdIn", groupId,
                        "resourceType", resourceType,
                        "resourceId", processKey));
    }

    public List<Map> getAuthorizationsByResource(AuthorizationRequest authorizationRequest) {
        log.info("Get authorizations for userId: {} or groupId: {}, processKey: {}"
                        + " and resourceType: {}", authorizationRequest.getUserId(), authorizationRequest.getGroupId(),
                authorizationRequest.getResourceId(), authorizationRequest.getResourceType());
        Map<String, String> params = new HashMap<>();

        if (authorizationRequest.getUserId() == null) {
            params.put("groupIdIn", Objects.toString(authorizationRequest.getGroupId(), ""));
        } else {
            params.put("userIdIn", Objects.toString(authorizationRequest.getUserId(), ""));
        }
        params.put("resourceType", authorizationRequest.getResourceType().toString());
        params.put("resourceId", authorizationRequest.getResourceId());

        return getAuthorizationsByParams(params);
    }

    public void updateAuthorization(String authorizationId, AuthorizationRequest updateAuthorizationRequest) {
        log.info("Authorization id to update: {}", authorizationId);

        given()
                .spec(requestSpec)
                .pathParam("id", authorizationId)
                .body(updateAuthorizationRequest, ObjectMapperType.JACKSON_2)
                .when()
                .put("api/authorization/{id}")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract()
                .response()
                .statusCode();

        log.info("Authorization updated: {}", authorizationId);
    }

    public void deleteAuthorization(String authorizationId) {
        log.info("Authorization id to delete: {}", authorizationId);
        given()
                .spec(requestSpec)
                .pathParam("id", authorizationId)
                .delete("api/authorization/{id}")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        log.info("Authorization deleted: {}", authorizationId);
    }
}
