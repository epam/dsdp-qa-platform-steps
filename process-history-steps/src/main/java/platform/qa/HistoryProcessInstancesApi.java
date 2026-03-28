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

package platform.qa;

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
import platform.qa.entities.ErrorResponse;
import platform.qa.entities.HistoryProcessInstance;
import platform.qa.entities.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;

@Log4j2
public class HistoryProcessInstancesApi {
    private final RequestSpecification requestSpec;

    public HistoryProcessInstancesApi(Service processHistory) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setConfig(
                        config()
                                .logConfig(logConfig()
                                        .enableLoggingOfRequestAndResponseIfValidationFails()
                                        .enablePrettyPrinting(Boolean.TRUE)))
                .setBaseUri(processHistory.getUrl() + "api/history")
                .setContentType(ContentType.JSON)
                .addHeader(X_ACCESS_TOKEN_HEADER, processHistory.getUser().getToken())
                .addHeader(XSRF_HEADER_NAME, XSRF_HEADER_VALUE)
                .addHeader(COOKIE_HEADER_NAME, COOKIE_HEADER_VALUE);

        requestSpec = requestSpecBuilder.build();
    }

    public List<HistoryProcessInstance> getHistoryProcessInstances() {
        log.info("Retrieve a list of historical data of processes");
        requestSpec.queryParams(Map.of("limit", "100", "sort", "desc(endTime)"));

        return Arrays.asList(
                given()
                        .spec(requestSpec)
                        .get("/process-instances")
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract().as(HistoryProcessInstance[].class));
    }

    public ErrorResponse getHistoryProcessInstancesInvalidParam(Map<String, ?> parametersMap, int statusCode) {
        return given()
                .spec(requestSpec)
                .queryParams(parametersMap)
                .get("/process-instances")
                .then()
                .statusCode(statusCode)
                .extract().as(ErrorResponse.class);
    }

    public ErrorResponse getHistoryProcessInstances(int statusCode) {
        log.info("Getting a list of historical data of processes should return error with status: {}", statusCode);
        return given()
                .spec(requestSpec)
                .get("/process-instances")
                .then()
                .statusCode(statusCode)
                .extract().as(ErrorResponse.class);
    }
}
