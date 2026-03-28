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
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Definition;
import platform.qa.entities.Instance;
import platform.qa.entities.Service;
import platform.qa.entities.User;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;

/**
 * Implement abilities to manipulate process definition in user process management
 */
@Log4j2
public class ProcessDefinitionApi {

    private final RequestSpecification requestSpec;

    public ProcessDefinitionApi(Service bpms) {
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

    public ProcessDefinitionApi(String url, User user) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setConfig(
                        config()
                                .logConfig(logConfig()
                                        .enableLoggingOfRequestAndResponseIfValidationFails()
                                        .enablePrettyPrinting(Boolean.TRUE)))
                .setBaseUri(url)
                .setContentType(ContentType.JSON)
                .addHeader(X_ACCESS_TOKEN_HEADER, user.getToken())
                .addHeader(XSRF_HEADER_NAME, XSRF_HEADER_VALUE)
                .addHeader(COOKIE_HEADER_NAME, COOKIE_HEADER_VALUE);

        requestSpec = requestSpecBuilder.build();
    }

    public List<String> getAllDefinitionKeys() {
        log.info("Find all definition keys");
        String processDefinitionPath = "api/process-definition";
        return given()
                .spec(requestSpec)
                .get(processDefinitionPath)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getList("key");
    }

    public List<Definition> getAllDefinitions() {
        log.info("Find all definitions");
        String processDefinitionPath = "api/process-definition";
        return given()
                .spec(requestSpec)
                .get(processDefinitionPath)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getList("", Definition.class);
    }

    public String getDefinitionNameByKey(String processId) {
        log.info("Find definition name by key {}", processId);
        String processDefinitionPath = "api/process-definition/key/" + processId;
        String name = given()
                .spec(requestSpec)
                .get(processDefinitionPath)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .get("name")
                .toString();

        log.info("process-definition name: {}", name);
        return name;
    }

    public String getDefinitionIdByName(String definitionName) {
        log.info("Find definition id by name {}", definitionName);
        String definitionId = given()
                .spec(requestSpec)
                .queryParam("name", definitionName)
                .get("api/process-definition")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getString("id[0]");

        log.info("process-definition id: {}", definitionId);
        return definitionId;
    }

    public Map getDefinitionById(String definitionId) {
        log.info("Find process-definition data by definitionId: {}", definitionId);
        return given()
                .spec(requestSpec)
                .pathParam("definitionId", definitionId)
                .get("api/process-definition/{definitionId}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getMap("");
    }

    public List getDefinitionByKey(String processDefinitionKey) {
        log.info("Find process-definition data by processDefinitionKey: {}", processDefinitionKey);
        return given()
                .spec(requestSpec)
                .pathParam("processDefinitionKey", processDefinitionKey)
                .get("api/process-definition?processDefinitionKey={processDefinitionKey}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .contentType(ContentType.JSON)
                .extract()
                .as(List.class);
    }

    public Map getDefinitionByName(String definitionName) {
        log.info("Retrieve process definition by name= {}", definitionName);
        return given()
                .spec(requestSpec)
                .queryParam("name", definitionName)
                .get("api/process-definition")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getMap("[0]");
    }

    public Definition getProcessDefinitionByName(String definitionName) {
        log.info("Find process-definition data by definitionName: {}", definitionName);
        return given()
                .spec(requestSpec)
                .queryParam("name", definitionName)
                .get("api/process-definition")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getObject("[0]", Definition.class);
    }

    public boolean deleteDefinitionByKey(String defKey) {
        log.info("Delete process-definition by key: {}", defKey);
        String processDefinitionPath = "api/process-definition/key/".concat(defKey).concat("/?cascade=true");
        int statusCode = given()
                .spec(requestSpec)
                .delete(processDefinitionPath)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .getStatusCode();
        return statusCode == HttpStatus.SC_OK;
    }

    public String startProcessInstance(String defKey) {
        log.info("Start process instance for definition key: {}", defKey);
        return given()
                .spec(requestSpec)
                .body("{}")
                .post("api/process-definition/key/" + defKey + "/start")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .extract()
                .response()
                .jsonPath()
                .get("id")
                .toString();
    }

    public Instance startProcessInstanceWithInitiator(String defKey, String userId) {
        log.info("Start process instance for definition key: {} by userId: {}", defKey, userId);
        Instance instance = given()
                .spec(requestSpec)
                .body(Map.of("variables", Map.of("initiator", Map.of("value", userId, "type", "String")),
                        "businessKey", RandomStringUtils.secure().nextAlphanumeric(8)))
                .post("api/process-definition/key/" + defKey + "/start")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .extract()
                .response()
                .jsonPath()
                .getObject("", Instance.class);

        log.info("Process instance started: {}", instance);
        return instance;
    }

    public Definition getProcessDefinitionById(String id) {
        Definition definition = given()
                .spec(requestSpec)
                .get("api/process-definition/" + id)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .extract()
                .response()
                .jsonPath()
                .getObject("", Definition.class);

        log.info("Process definition found By Id: {}", definition);
        return definition;
    }

    public Instance startProcessInstanceByDefinitionId(String id) {
        Instance instance = given()
                .spec(requestSpec)
                .body("{}")
                .post("api/process-definition/" + id + "/start")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(empty()))
                .extract()
                .response()
                .jsonPath()
                .getObject("", Instance.class);

        log.info("Process instance: {} was started by definition Id: {}", instance, id);
        return instance;
    }

    public int suspendProcessDefinitionById(boolean suspended, String definitionId) {
        int statusCode = given()
                .spec(requestSpec)
                .body(Map.of("suspended", suspended, "includeProcessInstances", Boolean.TRUE))
                .put("api/process-definition/" + definitionId + "/suspended/")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract()
                .response()
                .getStatusCode();

        log.info("Process definition with id {}  Suspend: {}", definitionId, suspended);
        return statusCode;
    }

    public int suspendProcessDefinitionByKey(boolean suspended, String key) {
        int statusCode = given()
                .spec(requestSpec)
                .body(Map.of("suspended", suspended, "includeProcessInstances", Boolean.TRUE))
                .put("api/process-definition/key/" + key + "/suspended/")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .extract()
                .response()
                .getStatusCode();

        log.info("Process definition by key {}  Suspend: {}", key, suspended);
        return statusCode;
    }

    private List<Definition> getDefinitionsByParams(Map<String, ?> queryParams) {
        log.info("Get definitions by parameters: {}", queryParams);
        return given()
                .spec(requestSpec)
                .queryParams(queryParams)
                .get("api/process-definition/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", Definition.class);
    }

    public void deleteCreatedProcessDefinitions() {
        List<Definition> createdDefinitions = getDefinitionsByParams(Map.of("keyLike", "%_AUTO"));
        if (createdDefinitions != null && !createdDefinitions.isEmpty()) {
            createdDefinitions.forEach(definition -> deleteDefinitionByKey(definition.getKey()));
        }
    }
}
