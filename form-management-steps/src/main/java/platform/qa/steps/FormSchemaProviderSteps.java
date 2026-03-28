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

package platform.qa.steps;

import io.restassured.response.Response;
import platform.qa.api.FormSchemaProviderApi;
import platform.qa.entities.Service;
import platform.qa.entity.ErrorMessageResponse;

import java.util.Collections;
import java.util.Map;
import org.apache.http.HttpStatus;


public class FormSchemaProviderSteps {

    public static final String FORM_SCHEMA_NOT_FOUND = "FORM_SCHEMA_NOT_FOUND";
    private final FormSchemaProviderApi schemaProviderApi;

    public FormSchemaProviderSteps(Service service) {
        schemaProviderApi = new FormSchemaProviderApi(service);
    }

    public boolean isFormWasFoundByName(String formName) {
        return formName.equalsIgnoreCase(String.valueOf(getFormByName(formName).get("name")));
    }

    public Map getFormByName(String formName) {
        Response response = schemaProviderApi.getSearchFormByName(formName);
        int statusCode = response.statusCode();

        if (HttpStatus.SC_OK == statusCode) {
            return response.body().as(Map.class);
        } else if (isTypeErrorFormSchemaNotFound(response)) {
            return Collections.emptyMap();
        } else {
            throw new IllegalStateException(response.prettyPrint());
        }
    }

    private boolean isTypeErrorFormSchemaNotFound(Response response) {
        return HttpStatus.SC_NOT_FOUND == response.statusCode() &&
                FORM_SCHEMA_NOT_FOUND.equals(response.body().as(ErrorMessageResponse.class).getCode());
    }

}
