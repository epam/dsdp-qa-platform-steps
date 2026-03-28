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
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static platform.qa.constants.Constants.COOKIE_HEADER_NAME;
import static platform.qa.constants.Constants.COOKIE_HEADER_VALUE;
import static platform.qa.constants.Constants.XSRF_HEADER_NAME;
import static platform.qa.constants.Constants.XSRF_HEADER_VALUE;
import static platform.qa.constants.Constants.X_ACCESS_TOKEN_HEADER;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.entities.Task;
import platform.qa.entities.TaskHistory;

import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;

/**
 * Implement abilities to manipulate tasks in user task management
 */
@Log4j2
public class TaskApi {

    private final RequestSpecification requestSpec;

    public TaskApi(Service bpms) {
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

        requestSpec = requestSpecBuilder.build();
    }

    public List<Task> getTasksInstances() {
        log.info("Get tasks instances as List");
        return given()
                .spec(requestSpec)
                .get("api/task/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", Task.class);
    }

    public int getTaskCountByProcessInstanceId(String id) {
        log.info("Get tasks count by processInstanceId: {}", id);
        int count = given()
                .spec(requestSpec)
                .queryParam("processInstanceId", id)
                .get("api/task/count")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .get("count");
        log.info("Tasks count: {} by processInstanceId {}", count, id);
        return count;
    }

    public int getTaskCountByProcessDefinitionId(String id) {
        log.info("Get tasks count by processDefinitionId: {}", id);
        int count = given()
                .spec(requestSpec)
                .queryParam("processDefinitionId", id)
                .get("api/task/count")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .get("count");
        log.info("Tasks count: {} by processDefinitionId: {}", count, id);
        return count;
    }

    public List<Task> getTasksByProcessInstanceId(String id) {
        log.info("Get tasks by processInstanceId: {}", id);
        return given()
                .spec(requestSpec)
                .queryParam("processInstanceId", id)
                .get("api/task")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", Task.class);
    }

    public List<Task> getTasksByProcessDefinitionId(String id) {
        log.info("Get tasks by processDefinitionId: {}", id);
        return given()
                .spec(requestSpec)
                .queryParam("processDefinitionId", id)
                .get("api/task")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", Task.class);
    }

    public List<Task> getTasksByProcessDefinitionName(String name) {
        log.info("Get tasks by processDefinitionName: {}", name);
        return getTasksByParams(Map.of("processDefinitionName", name));
    }

    /**
     * will be replaced with History Management API (https://jiraeu.epam.com/browse/MDTUDDM-10595)
     */
    @Deprecated
    public List<TaskHistory> getTasksHistoryByDefinitionId(String id) {
        log.info("Get history/task by processDefinitionId: {}", id);
        return given()
                .spec(requestSpec)
                .queryParam("processDefinitionId", id)
                .get("api/history/task")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", TaskHistory.class);
    }

    public void setTaskAssignee(String taskId, String userId) {
        log.info("Set task assignee userId: {}, taskId: {}", userId, taskId);
        given()
                .spec(requestSpec)
                .pathParam("taskId", taskId)
                .body(Map.of("userId", userId))
                .when()
                .post("api/task/{taskId}/assignee")
                .then()
                .statusCode(SC_NO_CONTENT);
    }

    public void claimTaskById(String id, String userId) {
        log.info("Claim task: {} by user: {}", id, userId);
        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .when()
                .body(Map.of("userId", userId))
                .post("api/task/" + id + "/claim")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    public void completeTaskById(String id) {
        log.info("Complete task by id: {}", id);
        var payload = Map.of("data", Map.of("firstName", Map.of("value", "name")));
        given()
                .spec(requestSpec)
                .pathParam("taskId", id)
                .body(payload)
                .when()
                .post("api/task/{taskId}/complete")
                .then()
                .statusCode(SC_NO_CONTENT);
    }

    /**
     * will be replaced with History Management API (https://jiraeu.epam.com/browse/MDTUDDM-10595)
     */
    @Deprecated
    public List<TaskHistory> getTasksHistoryByProcessInstanceId(String id) {
        log.info("Get history/task list by processInstanceId: {}", id);
        return given()
                .spec(requestSpec)
                .queryParam("processInstanceId", id)
                .get("api/history/task")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", TaskHistory.class);
    }

    /**
     * will be replaced with History Management API (https://jiraeu.epam.com/browse/MDTUDDM-10595)
     */
    @Deprecated
    public List<TaskHistory> getTasksHistory() {
        log.info("Get all history/task list");
        return given()
                .spec(requestSpec)
                .get("api/history/task")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", TaskHistory.class);
    }

    public Task getTaskById(String id) {
        log.info("Get task by id: {}", id);
        return given()
                .spec(requestSpec)
                .pathParam("id", id)
                .get("api/task/{id}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath()
                .getObject("", Task.class);
    }

    public List<Task> getTasksByName(String name) {
        log.info("Get task by name: {}", name);
        return getTasksByParams(Map.of("name", name));
    }

    public List<Task> getTasksByAssignee(String assignee) {
        log.info("Get task by assignee: {}", assignee);
        return getTasksByParams(Map.of("assignee", assignee));
    }

    private List<Task> getTasksByParams(Map<String, ?> queryParams) {
        log.info("Get task list by parameters: {}", queryParams);

        return given()
                .spec(requestSpec)
                .queryParams(queryParams)
                .get("api/task/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", Task.class);
    }
}