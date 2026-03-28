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

package platform.qa.data.common;

import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.entities.Subject;
import platform.qa.pojo.common.SubjectSettings;
import platform.qa.rest.RestApiClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.reflect.TypeToken;

/**
 * Search conditions by subject
 */
@Log4j2
public class SubjectSearchConditions {

    private final String SUBJECT_EQUAL_SUBJECT_TYPE = "subject-equal-subject-type-equal-subject-code/";
    private final String SUBJECT_SETTINGS_EQUAL = "subject-settings-equal-settings-id/";

    private Service dataFactory;

    public SubjectSearchConditions(Service service) {
        dataFactory = service;
    }

    public Subject subjectEqual(String subjectType, String subjectCode) {
        log.info("Запит до SC subject-equal-subject-type-equal-subject-code");
        if (subjectType == null || subjectCode == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("subjectType", subjectType);
        queryParams.put("subjectCode", subjectCode);
        return new RestApiClient(dataFactory)
                .sendGetWithParams(SUBJECT_EQUAL_SUBJECT_TYPE, queryParams)
                .extract()
                .jsonPath()
                .getObject("[0]", Subject.class);
    }


    public List<SubjectSettings> subjectSettings(String settingsId) {
        log.info("Запит до SC subject-settings-equal-settings-id");
        if (settingsId == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("settingsId", settingsId);
        return new RestApiClient(dataFactory)
                .sendGetWithParams(SUBJECT_SETTINGS_EQUAL, queryParams)
                .extract()
                .as(new TypeToken<List<SubjectSettings>>() {}.getType());
    }
}
