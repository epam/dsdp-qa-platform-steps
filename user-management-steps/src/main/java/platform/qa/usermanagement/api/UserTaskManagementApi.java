package platform.qa.usermanagement.api;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static platform.qa.usermanagement.constants.Constants.COOKIE_HEADER_NAME;
import static platform.qa.usermanagement.constants.Constants.COOKIE_HEADER_VALUE;
import static platform.qa.usermanagement.constants.Constants.XSRF_HEADER_NAME;
import static platform.qa.usermanagement.constants.Constants.XSRF_HEADER_VALUE;
import static platform.qa.usermanagement.constants.Constants.X_ACCESS_TOKEN_HEADER;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.usermanagement.pojo.response.CompleteResponse;
import platform.qa.usermanagement.pojo.response.CountResponse;
import platform.qa.usermanagement.pojo.response.ErrorResponse;
import platform.qa.usermanagement.pojo.response.UserTaskLightweightResponse;
import platform.qa.usermanagement.pojo.response.UserTaskResponse;
import platform.qa.usermanagement.pojo.response.UserTaskWithDataResponse;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class UserTaskManagementApi {
    private final RequestSpecification requestSpec;

    public UserTaskManagementApi(Service taskMng) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setConfig(
                        config()
                                .logConfig(logConfig()
                                        .enableLoggingOfRequestAndResponseIfValidationFails()
                                        .enablePrettyPrinting(Boolean.TRUE)))
                .setBaseUri(taskMng.getUrl() + "user-task-management/api")
                .setContentType(ContentType.JSON)
                .addHeader(X_ACCESS_TOKEN_HEADER, taskMng.getUser().getToken())
                .addHeader(XSRF_HEADER_NAME, XSRF_HEADER_VALUE)
                .addHeader(COOKIE_HEADER_NAME, COOKIE_HEADER_VALUE);

        requestSpec = requestSpecBuilder.build();
    }

    // ============ Get Tasks Methods ============

    public List<UserTaskResponse> getTasks() {
        log.info("Get all user tasks");
        return Arrays.asList(given()
                .spec(requestSpec)
                .when()
                .get("/task")
                .then()
                .statusCode(SC_OK)
                .extract().as(UserTaskResponse[].class));
    }

    public ErrorResponse getTasks(int statusCode) {
        log.info("Get tasks should return error with status: {}", statusCode);
        return given()
                .spec(requestSpec)
                .when()
                .get("/task")
                .then()
                .statusCode(statusCode)
                .extract().as(ErrorResponse.class);
    }

    public List<UserTaskResponse> getTasksByProcessInstanceId(String processInstanceId) {
        log.info("Get tasks for process instance: {}", processInstanceId);
        return Arrays.asList(given()
                .spec(requestSpec)
                .queryParam("processInstanceId", processInstanceId)
                .when()
                .get("/task")
                .then()
                .statusCode(SC_OK)
                .extract().as(UserTaskResponse[].class));
    }


    // ============ Get Lightweight Tasks Methods ============

    public List<UserTaskLightweightResponse> getLightweightTasks() {
        log.info("Get all lightweight user tasks");
        return Arrays.asList(given()
                .spec(requestSpec)
                .when()
                .get("/task/lightweight")
                .then()
                .statusCode(SC_OK)
                .extract().as(UserTaskLightweightResponse[].class));
    }

    public List<UserTaskLightweightResponse> getLightweightTasksByRootProcessInstanceId(String rootProcessInstanceId) {
        log.info("Get lightweight tasks for root process instance: {}", rootProcessInstanceId);
        return Arrays.asList(given()
                .spec(requestSpec)
                .queryParam("rootProcessInstanceId", rootProcessInstanceId)
                .when()
                .get("/task/lightweight")
                .then()
                .statusCode(SC_OK)
                .extract().as(UserTaskLightweightResponse[].class));
    }

    public ErrorResponse getLightweightTasksByRootProcessInstanceId(String rootProcessInstanceId, int statusCode) {
        log.info("Get lightweight tasks for root process instance: {} should return error with status: {}",
                rootProcessInstanceId, statusCode);
        return given()
                .spec(requestSpec)
                .queryParam("rootProcessInstanceId", rootProcessInstanceId)
                .when()
                .get("/task/lightweight")
                .then()
                .statusCode(statusCode)
                .extract().as(ErrorResponse.class);
    }


    // ============ Count Tasks ============

    public CountResponse countTasks() {
        log.info("Get count of user tasks");
        return given()
                .spec(requestSpec)
                .when()
                .get("/task/count")
                .then()
                .statusCode(SC_OK)
                .extract().as(CountResponse.class);
    }

    public ErrorResponse countTasks(int statusCode) {
        log.info("Get count of user tasks should return error with status: {}", statusCode);
        return given()
                .spec(requestSpec)
                .when()
                .get("/task/count")
                .then()
                .statusCode(statusCode)
                .extract().as(ErrorResponse.class);
    }

    // ============ Get Task By Id ============

    public UserTaskWithDataResponse getTaskById(String taskId) {
        log.info("Get task by taskId: {}", taskId);
        return given()
                .spec(requestSpec)
                .pathParam("id", taskId)
                .when()
                .get("/task/{id}")
                .then()
                .statusCode(SC_OK)
                .extract().as(UserTaskWithDataResponse.class);
    }

    public ErrorResponse getTaskById(String taskId, int statusCode) {
        log.info("Get task by taskId: {} should return error with status: {}", taskId, statusCode);
        return given()
                .spec(requestSpec)
                .pathParam("id", taskId)
                .when()
                .get("/task/{id}")
                .then()
                .statusCode(statusCode)
                .extract().as(ErrorResponse.class);
    }

    // ============ Claim Task ============

    public void claimTaskById(String taskId) {
        log.info("Claim task by taskId: {}", taskId);
        given()
                .spec(requestSpec)
                .pathParam("taskId", taskId)
                .when()
                .post("/task/{taskId}/claim")
                .then()
                .statusCode(SC_NO_CONTENT);
    }

    public ErrorResponse claimTaskById(String taskId, int statusCode) {
        log.info("Negative path. Claim task by taskId: {} should return error with status: {}", taskId, statusCode);
        return given()
                .spec(requestSpec)
                .pathParam("taskId", taskId)
                .when()
                .post("/task/{taskId}/claim")
                .then()
                .statusCode(statusCode)
                .extract().as(ErrorResponse.class);
    }

    // ============ Complete Task ============

    public CompleteResponse completeTaskById(String taskId, Object payload) {
        log.info("Complete task by taskId: {}", taskId);
        return given()
                .spec(requestSpec)
                .pathParam("taskId", taskId)
                .body(payload)
                .when()
                .post("/task/{taskId}/complete")
                .then()
                .statusCode(SC_OK)
                .extract().as(CompleteResponse.class);
    }

    public ErrorResponse completeTaskById(String taskId, Object payload, int statusCode) {
        log.info("Negative path. Complete task by taskId: {}", taskId);
        return given()
                .spec(requestSpec)
                .pathParam("taskId", taskId)
                .body(payload)
                .when()
                .post("/task/{taskId}/complete")
                .then()
                .statusCode(statusCode)
                .extract().as(ErrorResponse.class);
    }

    // ============ Save Task ============

    public void saveTaskById(String taskId, Object payload) {
        log.info("Save task by taskId: {}", taskId);
        given()
                .spec(requestSpec)
                .pathParam("taskId", taskId)
                .body(payload)
                .when()
                .post("/task/{taskId}/save")
                .then()
                .statusCode(SC_OK);
    }

    public ErrorResponse saveTaskById(String taskId, Object payload, int statusCode) {
        log.info("Negative path. Save task by taskId: {}", taskId);
        return given()
                .spec(requestSpec)
                .pathParam("taskId", taskId)
                .body(payload)
                .when()
                .post("/task/{taskId}/save")
                .then()
                .statusCode(statusCode)
                .extract().as(ErrorResponse.class);
    }
}
