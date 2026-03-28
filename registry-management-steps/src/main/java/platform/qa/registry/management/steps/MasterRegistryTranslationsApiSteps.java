/*
 * Copyright 2024 EPAM Systems.
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

package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.GET_TRANSLATIONS_LIST_MASTER_VERSION;
import static platform.qa.registry.management.enumeration.Urls.GET_TRANSLATION_MASTER_VERSION;

import io.restassured.http.ContentType;
import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.EntityInfo;
import platform.qa.registry.management.dto.response.Translation;
import platform.qa.registry.management.enumeration.Urls;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class MasterRegistryTranslationsApiSteps extends BaseStep {

    public MasterRegistryTranslationsApiSteps(Service service) {
        super(service);
    }

    public List<Translation> getTranslationsList() {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(GET_TRANSLATIONS_LIST_MASTER_VERSION.getUrl(),
                        null,
                        new TypeReference<List<Translation>>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String getTranslationContent(String languageName) {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(GET_TRANSLATION_MASTER_VERSION.getUrl().replace(LANGUAGE_NAME, languageName),
                        null,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String createTranslation(String content, String languageName) {
        return new RestClientProxy(service)
                .positiveRequest()
                .post(Urls.CREATE_TRANSLATION_MASTER_VERSION.getUrl().replace(LANGUAGE_NAME, languageName),
                        null,
                        content,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_CREATED
                );
    }

    public String updateTranslation(String content, String languageName, Map<String, String> headers) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.JSON)
                .put(Urls.UPDATE_TRANSLATION_MASTER_VERSION.getUrl().replace(LANGUAGE_NAME, languageName),
                        null,
                        content,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK,
                        headers
                );
    }

    public void deleteTranslation(String languageName, Map<String, String> headers) {
        new RestClientProxy(service)
                .positiveRequest(ContentType.JSON)
                .delete(Urls.DELETE_TRANSLATION_MASTER_VERSION.getUrl().replace(LANGUAGE_NAME, languageName),
                        HttpStatus.SC_NO_CONTENT,
                        headers
                );
    }
}