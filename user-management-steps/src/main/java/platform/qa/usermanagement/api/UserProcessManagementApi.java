package platform.qa.usermanagement.api;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static org.apache.http.HttpStatus.SC_OK;
import static platform.qa.usermanagement.constants.Constants.COOKIE_HEADER_NAME;
import static platform.qa.usermanagement.constants.Constants.COOKIE_HEADER_VALUE;
import static platform.qa.usermanagement.constants.Constants.XSRF_HEADER_NAME;
import static platform.qa.usermanagement.constants.Constants.XSRF_HEADER_VALUE;
import static platform.qa.usermanagement.constants.Constants.X_ACCESS_TOKEN_HEADER;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.usermanagement.pojo.response.CountResponse;
import platform.qa.usermanagement.pojo.response.ErrorResponse;
import platform.qa.usermanagement.pojo.response.ProcessDefinitionResponse;
import platform.qa.usermanagement.pojo.response.StartProcessInstanceResponse;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class UserProcessManagementApi {
    private final RequestSpecification requestSpec;

    public UserProcessManagementApi(Service processMng) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setConfig(
                        config()
                                .logConfig(logConfig()
                                        .enableLoggingOfRequestAndResponseIfValidationFails()
                                        .enablePrettyPrinting(Boolean.TRUE)))
                .setBaseUri(processMng.getUrl() + "user-process-management/api/")
                .setContentType(ContentType.JSON)
                .addHeader(X_ACCESS_TOKEN_HEADER, processMng.getUser().getToken())
                .addHeader(XSRF_HEADER_NAME, XSRF_HEADER_VALUE)
                .addHeader(COOKIE_HEADER_NAME, COOKIE_HEADER_VALUE);

        requestSpec = requestSpecBuilder.build();
    }

    public StartProcessInstanceResponse startProcess(String key) {
        log.info("Start process with key: {}", key);
        return given()
                .spec(requestSpec)
                .pathParam("key", key)
                .when()
                .post("/process-definition/{key}/start")
                .then()
                .statusCode(SC_OK).extract().as(StartProcessInstanceResponse.class);
    }

    public ErrorResponse startProcess(String key, int statusCode) {
        log.info("Start process with key: {} should return Error: {}", key, statusCode);
        return given()
                .spec(requestSpec)
                .pathParam("key", key)
                .when()
                .get("/process-definition/{key}")
                .then()
                .statusCode(statusCode)
                .extract()
                .as(ErrorResponse.class);
    }

    public StartProcessInstanceResponse startProcessWithForm(String processDefinitionKey, Object formPayload) {
        log.info("Start with form for process with key: {}", processDefinitionKey);
        return given()
                .spec(requestSpec)
                .pathParam("processDefinitionKey", processDefinitionKey)
                .body(formPayload)
                .when()
                .post("/process-definition/{processDefinitionKey}/start-with-form")
                .then()
                .statusCode(SC_OK)
                .extract().as(StartProcessInstanceResponse.class);
    }

    public ErrorResponse startProcessWithForm(String processDefinitionKey, Object formPayload, int statusCode) {
        ValidatableResponse response = given()
                .spec(requestSpec)
                .pathParam("processDefinitionKey", processDefinitionKey)
                .body(formPayload)
                .when()
                .post("/process-definition/{processDefinitionKey}/start-with-form")
                .then()
                .statusCode(statusCode);
        log.info("Negative path. Start with form for process with key: {}", processDefinitionKey);
        return response.extract().as(ErrorResponse.class);
    }

    public ProcessDefinitionResponse getProcessDefinitionByKey(String key) {
        log.info("Get process definition by key: {}", key);
        return given()
                .spec(requestSpec)
                .pathParam("key", key)
                .when()
                .get("/process-definition/{key}")
                .then()
                .statusCode(SC_OK)
                .extract().as(ProcessDefinitionResponse.class);
    }

    public ErrorResponse getProcessDefinitionByKey(String key, int statusCode) {
        log.info("Get process definition by key: {} should return Error: {}", key, statusCode);
        return given()
                .spec(requestSpec)
                .pathParam("key", key)
                .when()
                .get("/process-definition/{key}")
                .then()
                .statusCode(statusCode)
                .extract()
                .as(ErrorResponse.class);
    }

    public List<ProcessDefinitionResponse> getProcessDefinitions() {
        log.info("Get list of process definitions");
        return Arrays.asList(
                given()
                        .spec(requestSpec)
                        .when()
                        .get("/process-definition")
                        .then()
                        .statusCode(SC_OK)
                        .extract().as(ProcessDefinitionResponse[].class));
    }

    public List<ProcessDefinitionResponse> getProcessDefinitions(boolean active, boolean suspended) {
        log.info("Get list of process definitions with params - active: {}, suspended: {}", active, suspended);
        return Arrays.asList(
                given()
                        .spec(requestSpec)
                        .queryParam("active", active)
                        .queryParam("suspended", suspended)
                        .when()
                        .get("/process-definition")
                        .then()
                        .statusCode(SC_OK)
                        .extract().as(ProcessDefinitionResponse[].class));
    }

    public CountResponse getCountProcessDefinitions() {
        log.info("Get count of process definitions");
        return given()
                .spec(requestSpec)
                .when()
                .get("/process-definition/count")
                .then()
                .statusCode(SC_OK)
                .extract().as(CountResponse.class);
    }

    public CountResponse getCountProcessDefinitions(boolean active, boolean suspended) {
        log.info("Get count of process definitions with params - active: {}, suspended: {}", active, suspended);
        return given()
                .spec(requestSpec)
                .queryParam("active", active)
                .queryParam("suspended", suspended)
                .when()
                .get("/process-definition/count")
                .then()
                .statusCode(SC_OK)
                .extract().as(CountResponse.class);
    }

    public ErrorResponse getCountProcessDefinitions(int statusCode) {
        log.info("Get process definition count should return Error: {}", statusCode);
        return given()
                .spec(requestSpec)
                .when()
                .get("/process-definition/count")
                .then()
                .statusCode(statusCode)
                .extract()
                .as(ErrorResponse.class);
    }

}
