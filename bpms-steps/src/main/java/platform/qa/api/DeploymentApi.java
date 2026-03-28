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
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Deployment;
import platform.qa.entities.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpStatus;

/**
 * Implement abilities to manipulate deployments in camunda
 */
@Log4j2
public class DeploymentApi {

    private final RequestSpecification requestSpec;

    public DeploymentApi(Service bpms) {
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

    public List<HashMap> getAllDeployments() {
        log.info("Fetching all deployments...");
        return given()
                .spec(requestSpec)
                .get("api/deployment")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath()
                .getList("");
    }

    public void deleteDeploymentById(String deploymentId) {
        log.info("Deleting deployment with ID: {}", deploymentId);

        given()
                .spec(requestSpec)
                .pathParam("id", deploymentId)
                .queryParam("cascade", true)
                .delete("api/deployment/{id}")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        log.info("Deployment deleted successfully: {}", deploymentId);
    }

    public Deployment createDeployment(File deployFile, String deploymentName) {
        log.info("Creating deployment '{}'", deploymentName);

        Deployment deployment = given()
                .spec(requestSpec)
                .contentType("multipart/form-data")
                .multiPart("deployment-name", deploymentName)
                .multiPart("upload", deployFile)
                .multiPart("enable-duplicate-filtering", "true")
                .post("api/deployment/create")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getObject("", Deployment.class);

        log.info("Deployment created successfully: {} (id: {})", deploymentName, deployment.getId());

        return deployment;
    }
}
