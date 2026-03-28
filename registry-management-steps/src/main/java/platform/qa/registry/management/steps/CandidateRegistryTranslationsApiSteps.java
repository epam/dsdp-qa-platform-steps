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

import platform.qa.entities.Service;
import platform.qa.registry.management.dto.request.form.Form;
import platform.qa.registry.management.dto.response.EntityInfo;
import platform.qa.registry.management.dto.response.Translation;
import platform.qa.registry.management.enumeration.Urls;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class CandidateRegistryTranslationsApiSteps extends BaseStep {

    public CandidateRegistryTranslationsApiSteps(Service service) {
        super(service);
    }

    public String getTranslationContent(String id, String languageName) {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(Urls.GET_TRANSLATION_FOR_SPECIFIC_VERSION.getUrl().replace(LANGUAGE_NAME, languageName).replace(ID, id),
                        null,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public List<Translation> getTranslationsList(String id) {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(Urls.GET_TRANSLATIONS_LIST_FOR_SPECIFIC_VERSION.getUrl().replace(ID, id),
                        null,
                        new TypeReference<List<Translation>>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String createTranslation(String id, String languageName, String content) {
        return new RestClientProxy(service)
                .positiveRequest()
                .post(Urls.CREATE_TRANSLATION_FOR_SPECIFIC_VERSION.getUrl().replace(ID, id).replace(LANGUAGE_NAME,
                                languageName),
                        null,
                        content,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_CREATED
                );
    }

    public String updateTranslation(String id, String languageName, String content, Map<String, String> headers) {
        return new RestClientProxy(service)
                .positiveRequest()
                .put(Urls.UPDATE_TRANSLATION_FOR_SPECIFIC_VERSION.getUrl().replace(LANGUAGE_NAME, languageName).replace(ID, id),
                        null,
                        content,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK,
                        headers
                );
    }

    public void deleteTranslation(String id, String languageName, Map<String, String> headers) {
        new RestClientProxy(service)
                .positiveRequest()
                .delete(Urls.DELETE_TRANSLATION_FOR_SPECIFIC_VERSION.getUrl().replace(LANGUAGE_NAME, languageName).replace(ID, id),
                        HttpStatus.SC_NO_CONTENT, headers
                );
    }
}
