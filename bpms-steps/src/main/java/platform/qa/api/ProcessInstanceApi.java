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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static platform.qa.constants.Constants.COOKIE_HEADER_NAME;
import static platform.qa.constants.Constants.COOKIE_HEADER_VALUE;
import static platform.qa.constants.Constants.XSRF_HEADER_NAME;
import static platform.qa.constants.Constants.XSRF_HEADER_VALUE;
import static platform.qa.constants.Constants.X_ACCESS_TOKEN_HEADER;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Instance;
import platform.qa.entities.InstanceHistory;
import platform.qa.entities.Service;

import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;

/**
 * Implement abilities to manipulate process instance in user process management
 */
@Log4j2
public class ProcessInstanceApi {

    private final RequestSpecification requestSpec;

    public ProcessInstanceApi(Service bpms) {
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

    public Map<String, Object> getProcessInstanceVariables(String processId) {
        log.info("Get process-instance variables by processId: {}", processId);
        return given()
                .spec(requestSpec)
                .get("api/process-instance/" + processId + "/variables")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .extract()
                .response()
                .jsonPath()
                .getMap("");
    }

    public int suspendProcessInstance(Boolean suspend, String instanceId) {
        log.info("Process instance: {}  Suspend: {}", instanceId, suspend);

        return given()
                .spec(requestSpec)
                .body(Map.of("suspended", suspend))
                .put("api/process-instance/" + instanceId + "/suspended")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract()
                .response()
                .getStatusCode();
    }

    public int deleteProcessInstance(String instanceId) {
        log.info("Delete process-instance by instanceId: {}", instanceId);
        Response response = given()
                .spec(requestSpec)
                .delete("api/process-instance/" + instanceId)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract()
                .response();

        return response.getStatusCode();
    }

    public void deleteProcessInstanceWithoutStatusCodeCheck(String instanceId) {
        log.info("Delete process-instance by instanceId: {} without status check", instanceId);
        given().spec(requestSpec).delete("api/process-instance/" + instanceId);
    }

    public String getProcessInstanceStatus(String instanceId) {
        log.info("Get process-instance suspended status by instanceId: {}", instanceId);
        String status = given()
                .spec(requestSpec)
                .get("api/process-instance/" + instanceId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .extract()
                .response()
                .jsonPath()
                .get("suspended")
                .toString();

        log.info("Process instance status: {}", status);
        return status;
    }

    public int getProcessInstanceStatusCode(String instanceId) {
        return getProcessInstanceStatusCode(instanceId, HttpStatus.SC_OK, true);
    }

    public int getProcessInstanceStatusCode(String instanceId, int expectedStatusCode) {
        return getProcessInstanceStatusCode(instanceId, expectedStatusCode, false);
    }

    private int getProcessInstanceStatusCode(String instanceId, int expectedStatusCode, boolean validateBodyNotEmpty) {
        log.info("Fetching process instance status code for instance ID: {}", instanceId);

        var request = given()
                .spec(requestSpec)
                .get("api/process-instance/" + instanceId)
                .then()
                .statusCode(expectedStatusCode);

        if (validateBodyNotEmpty) {
            request.body("$", not(empty()));
        }

        int statusCode = request.extract()
                .response()
                .getStatusCode();

        log.info("Process instance status code: {}", statusCode);
        return statusCode;
    }

    public List<Instance> getProcessInstancesList() {
        log.info("Get process instances as list");
        return given()
                .spec(requestSpec)
                .get("api/process-instance/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", Instance.class);
    }

    public int getProcessInstanceCount() {
        int count = given()
                .spec(requestSpec)
                .get("api/history/process-instance/count/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .extract()
                .response()
                .jsonPath()
                .get("count");

        log.info("Process instances count: {}", count);
        return count;
    }

    public Instance getProcessInstanceById(String id) {
        Instance instance = given()
                .spec(requestSpec)
                .get("api/process-instance/" + id)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .extract()
                .response()
                .jsonPath()
                .getObject("", Instance.class);

        log.info("Found process instance: {} by Id: {}", instance, id);
        return instance;
    }

    public String deleteProcessInstanceViaPost(String instanceId) {
        log.info("Delete process-instance by processInstanceId: {}", instanceId);
        String response = given()
                .spec(requestSpec)
                .body("{\n" +
                        "    \"deleteReason\": \"Terminating outdated processes\",\n" +
                        "    \"processInstanceIds\": [ \"" + instanceId + "\" ],\n" +
                        "    \"skipCustomListeners\" : false,\n" +
                        "    \"skipSubprocesses\" : false,\n" +
                        "    \"failIfNotExists\" : false\n" +
                        "}")
                .post("api/process-instance/delete")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .extract()
                .response()
                .jsonPath()
                .toString();

        log.info("Response: {}", response);
        return response;
    }

    /**
     * will be replaced with History Management API (https://jiraeu.epam.com/browse/MDTUDDM-10595)
     */
    @Deprecated
    public InstanceHistory getProcessInstanceHistoryById(String id) {
        log.info("Get history/process-instance by id: {}", id);
        InstanceHistory history = given()
                .spec(requestSpec)
                .get("api/history/process-instance/" + id)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .extract()
                .response()
                .jsonPath()
                .getObject("", InstanceHistory.class);

        log.info("Process instance history: {} by Id: {}", history, id);
        return history;
    }

    public String getProcessInstanceStateById(String id) {
        log.info("Get history/process-instance state by id: {}", id);
        String state = given()
                .spec(requestSpec)
                .get("api/history/process-instance/" + id)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getObject("", InstanceHistory.class)
                .getState();

        log.info("Process instance: {} has state: {}", id, state);
        return state;
    }

    private List<InstanceHistory> getProcessInstancesHistoryByParams(Map<String, ?> queryParams) {
        List<InstanceHistory> instanceList = given()
                .spec(requestSpec)
                .queryParams(queryParams)
                .get("api/history/process-instance/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", InstanceHistory.class);

        log.info("Got process instances history by params:{} list: {}", queryParams, instanceList);
        return instanceList;
    }

    private List<Instance> getProcessInstancesByParams(Map<String, ?> queryParams) {
        log.info("Get process-instance list by params:{}", queryParams);

        return given()
                .spec(requestSpec)
                .queryParams(queryParams)
                .get("api/process-instance/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", Instance.class);
    }
}