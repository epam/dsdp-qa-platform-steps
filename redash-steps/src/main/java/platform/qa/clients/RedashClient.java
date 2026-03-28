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

package platform.qa.clients;

import static io.restassured.RestAssured.given;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import platform.qa.entities.IEntity;
import platform.qa.entities.Service;
import platform.qa.pojo.redash.RedashUser;

import java.util.Map;


@Getter
public class RedashClient {
    private RequestSpecification requestSpec;
    private Service service;

    public RedashClient(Service redash, RedashUser user) {
        service = redash;

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setBaseUri(redash.getUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", user.getToken());
        requestSpec = requestSpecBuilder.build();
    }

    public RedashClient(Service redash) {
        service = redash;
    }

    public Object getRequest(String url, String field) {
        return given(getRequestSpecification(service))
                .get(url)
                .jsonPath()
                .get(field);
    }

    public void emptyPostRequest(String url, Map<String, String> params) {
        given(getRequestSpecification(service))
                .queryParams(params)
                .post(url);
    }

    public Object postRequest(String url, IEntity query) {
        return given(getRequestSpecification(service))
                .body(query)
                .post(url)
                .jsonPath()
                .get();
    }

    public void postRequestToSpecificUrl(String url, IEntity body) {
        RequestSpecBuilder specBuilder = new RequestSpecBuilder()
                .setBaseUri(service.getUrl().replace("/reports/api/", ""))
                .setContentType("multipart/form-data");

        given(specBuilder.build())
                .multiPart("password", "1231231231")
                .post(url);
    }

    public void deleteRequest(String url) {
        given(getRequestSpecification(service))
                .delete(url)
                .jsonPath()
                .get();
    }


    public RequestSpecification getRequestSpecification(Service redash) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setBaseUri(redash.getUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", redash.getToken());

        return requestSpecBuilder.build();
    }
}
