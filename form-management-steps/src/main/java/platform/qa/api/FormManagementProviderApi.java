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

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.entity.CreatedFormResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

@Log4j2
public class FormManagementProviderApi {
    private final RequestSpecification requestSpec;

    private static final String CREATE_FORM_ENDPOINT = "/form";
    private static final String GET_FORM_BY_ID_ENDPOINT = "/form/{formId}";
    private static final String GET_FORM_BY_NAME_ENDPOINT = "/form?name={formName}";
    private static final String GET_ALL_FORMS_ENDPOINT = "/form?type=form&limit=20000";

    public FormManagementProviderApi(Service fmpService) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setConfig(
                        config()
                                .logConfig(logConfig()
                                        .enableLoggingOfRequestAndResponseIfValidationFails()
                                        .enablePrettyPrinting(Boolean.TRUE)))
                .setBaseUri(fmpService.getUrl())
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Access-Token", fmpService.getUser().getToken())
                .addHeader("X-XSRF-TOKEN", "Token")
                .addHeader("Cookie", "XSRF-TOKEN=Token");

        requestSpec = requestSpecBuilder.build();
    }

    public List<Map> getAllForms() {
        log.info("Get all forms as list");
        return given()
                .spec(requestSpec)
                .get(GET_ALL_FORMS_ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getList("", Map.class);
    }

    public Map getFormById(String formId) {
        log.info("Get form by id: {}", formId);
        return given()
                .spec(requestSpec)
                .get(GET_FORM_BY_ID_ENDPOINT, formId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .response().body().as(Map.class);
    }

    public List<HashMap> getFormByName(String formName) {
        log.info("Get form by name: {}", formName);
        Response response = given()
                .spec(requestSpec)
                .get(GET_FORM_BY_NAME_ENDPOINT, formName)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .response();
        return (List<HashMap>) response.body().as(List.class);
    }

    public void deleteFormById(String formId) {
        log.info("Delete form by id: {}", formId);
        given()
                .spec(requestSpec)
                .delete(GET_FORM_BY_ID_ENDPOINT, formId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    public void deleteCreatedForms() {
        log.info("Delete all forms with 'AUTO' suffix");
        var allCreatedForms = getAllForms();
        IntStream.range(0, allCreatedForms.size()).forEach(i -> {
            String formName = String.valueOf(allCreatedForms.get(i).get("name"));
            if (StringUtils.containsIgnoreCase(formName, "AUTO")) {
                deleteFormById(String.valueOf(allCreatedForms.get(i).get("_id")));
            }
        });
    }

    public Map createForm(Object payload) {
        log.info("Create form by payload");
        return given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/form/")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .jsonPath()
                .getMap("");
    }

    public Map createForm(List<String> formFileLines) {
        log.info("Create form by file lines");
        Response response = given()
                .spec(requestSpec)
                .when()
                .body(StringUtils
                        .join(formFileLines, StringUtils.SPACE))
                .post(CREATE_FORM_ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();
        return response.body().as(Map.class);
    }

    public CreatedFormResponse createFormToObject(List<String> formFileLines) {
        log.info("Create form by file lines and get as CreatedFormResponse");
        Response response = given()
                .spec(requestSpec)
                .when()
                .body(StringUtils
                        .join(formFileLines, StringUtils.SPACE))
                .post(CREATE_FORM_ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        return response.body()
                .as(CreatedFormResponse.class, ObjectMapperType.GSON);
    }

    public void deleteFormByName(String formName) {
        log.info("Delete form by name: {}", formName);
        given()
                .spec(requestSpec)
                .delete(formName)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
