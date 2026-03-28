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

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import platform.qa.entities.Service;

@Log4j2
public class FormSchemaProviderApi {
  public static final String X_ACCESS_TOKEN = "X-Access-Token";
  private static final String CREATE_FORM_ENDPOINT = "/forms";
  private static final String GET_FORM_BY_NAME_ENDPOINT = "/forms/{formName}";
  private static final String PUT_FORM_BY_NAME_ENDPOINT = "/forms/{formName}";
  private final RequestSpecification requestSpec;
  private final String baseUrl;

  public FormSchemaProviderApi(Service formSchema) {
    this.baseUrl = formSchema.getUrl() + "/api";
    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    requestSpecBuilder
        .setConfig(
            config()
                .logConfig(
                    logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails()
                        .enablePrettyPrinting(Boolean.TRUE)))
        .setBaseUri(this.baseUrl)
        .addHeader("Content-Type", "application/json")
        .addHeader(X_ACCESS_TOKEN, formSchema.getUser().getToken())
        .addHeader("X-XSRF-TOKEN", "Token")
        .addHeader("Cookie", "XSRF-TOKEN=Token");

    requestSpec = requestSpecBuilder.build();
  }

  @Step("Get form by name: {formName}")
  public Map getFormByName(String formName) {
    log.info("Get form by name: {} as Map", formName);
    Response response =
        given()
            .spec(requestSpec)
            .get(GET_FORM_BY_NAME_ENDPOINT, formName)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .extract()
            .response();
    return response.body().as(Map.class);
  }

  @Step("Send GET request for form by name: {formName}")
  public Response getSearchFormByName(String formName) {
    log.info("Get form by name: {} as Response", formName);
    return given().spec(requestSpec).get(GET_FORM_BY_NAME_ENDPOINT, formName);
  }

  @Step("Delete form by name: {formName}")
  public void deleteForm(String formName) {
    log.info("Delete form by name: {} with status check", formName);
    given()
        .spec(requestSpec)
        .delete(GET_FORM_BY_NAME_ENDPOINT, formName)
        .then()
        .statusCode(HttpStatus.SC_NO_CONTENT);
  }

  @Step("Send DELETE request for form: {formName}")
  public Response sendDeleteForm(String formName) {
    log.info("Delete form by name: {}", formName);
    return given().spec(requestSpec).delete(GET_FORM_BY_NAME_ENDPOINT, formName);
  }

  @Step("Create form with payload")
  public void createForm(Object payload) {
    log.info("Create form by payload");
    given()
        .spec(requestSpec)
        .contentType(ContentType.JSON)
        .body(payload)
        .post(CREATE_FORM_ENDPOINT)
        .then()
        .statusCode(HttpStatus.SC_CREATED);
  }

  @Step("Create form from file lines")
  public void createForm(List<String> formFileLines) {
    log.info("Create form by file lines");
    given()
        .spec(requestSpec)
        .when()
        .body(StringUtils.join(formFileLines, StringUtils.SPACE))
        .post(CREATE_FORM_ENDPOINT)
        .then()
        .statusCode(HttpStatus.SC_CREATED);
  }

  @Step("Send POST request to create form")
  public Response sendPostForm(Object payloadForm) {
    log.info("Create form by payload and return as Response");
    return given()
        .spec(requestSpec)
        .contentType(ContentType.JSON)
        .body(payloadForm)
        .post(CREATE_FORM_ENDPOINT);
  }

  @Step("Send PUT request to update form: {formName}")
  public Response sendPutForm(Object payloadForm, String formName) {
    log.info("Update form: {} by payload and return as Response", formName);
    return given()
        .spec(requestSpec)
        .contentType(ContentType.JSON)
        .body(payloadForm)
        .put(PUT_FORM_BY_NAME_ENDPOINT, formName);
  }

  @Step("Update form: {formName}")
  public Response updateForm(Object payloadForm, String formName) {
    log.info("Update form: {} by payload", formName);
    return given()
        .spec(requestSpec)
        .contentType(ContentType.JSON)
        .body(payloadForm)
        .put(PUT_FORM_BY_NAME_ENDPOINT, formName);
  }

  // Methods for testing unauthorized scenarios

  @Step("Send POST request to create form without authentication")
  public Response sendPostFormWithoutAuth(Object payloadForm) {
    log.info("Create form without authentication");
    return given()
        .baseUri(baseUrl)
        .contentType(ContentType.JSON)
        .body(payloadForm)
        .post(CREATE_FORM_ENDPOINT);
  }

  @Step("Send POST request to create form with custom token")
  public Response sendPostFormWithCustomToken(Object payloadForm, String token) {
    log.info("Create form with custom token");
    return given()
        .baseUri(baseUrl)
        .contentType(ContentType.JSON)
        .header(X_ACCESS_TOKEN, token)
        .body(payloadForm)
        .post(CREATE_FORM_ENDPOINT);
  }

  @Step("Send GET request for form: {formName} without authentication")
  public Response sendGetFormWithoutAuth(String formName) {
    log.info("Get form: {} without authentication", formName);
    return given().baseUri(baseUrl).get(GET_FORM_BY_NAME_ENDPOINT, formName);
  }

  @Step("Send GET request for form: {formName} with custom token")
  public Response sendGetFormWithCustomToken(String formName, String token) {
    log.info("Get form: {} with custom token", formName);
    return given()
        .baseUri(baseUrl)
        .header(X_ACCESS_TOKEN, token)
        .get(GET_FORM_BY_NAME_ENDPOINT, formName);
  }

  @Step("Send PUT request to update form: {formName} without authentication")
  public Response sendPutFormWithoutAuth(Object payloadForm, String formName) {
    log.info("Update form: {} without authentication", formName);
    return given()
        .baseUri(baseUrl)
        .contentType(ContentType.JSON)
        .body(payloadForm)
        .put(PUT_FORM_BY_NAME_ENDPOINT, formName);
  }

  @Step("Send PUT request to update form: {formName} with custom token")
  public Response sendPutFormWithCustomToken(Object payloadForm, String formName, String token) {
    log.info("Update form: {} with custom token", formName);
    return given()
        .baseUri(baseUrl)
        .contentType(ContentType.JSON)
        .header(X_ACCESS_TOKEN, token)
        .body(payloadForm)
        .put(PUT_FORM_BY_NAME_ENDPOINT, formName);
  }

  @Step("Send DELETE request for form: {formName} without authentication")
  public Response sendDeleteFormWithoutAuth(String formName) {
    log.info("Delete form: {} without authentication", formName);
    return given().baseUri(baseUrl).delete(GET_FORM_BY_NAME_ENDPOINT, formName);
  }

  @Step("Send DELETE request for form: {formName} with custom token")
  public Response sendDeleteFormWithCustomToken(String formName, String token) {
    log.info("Delete form: {} with custom token", formName);
    return given()
        .baseUri(baseUrl)
        .header(X_ACCESS_TOKEN, token)
        .delete(GET_FORM_BY_NAME_ENDPOINT, formName);
  }

  @Step("Send POST request to create form with raw body")
  public Response sendPostFormRawBody(String rawBody) {
    log.info("Create form with raw body (for testing invalid JSON)");
    return given().spec(requestSpec).body(rawBody).post(CREATE_FORM_ENDPOINT);
  }

  @Step("Send PUT request to update form: {formName} with raw body")
  public Response sendPutFormRawBody(String rawBody, String formName) {
    log.info("Update form: {} with raw body (for testing invalid JSON)", formName);
    return given().spec(requestSpec).body(rawBody).put(PUT_FORM_BY_NAME_ENDPOINT, formName);
  }
}
