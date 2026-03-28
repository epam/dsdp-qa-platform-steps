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

package platform.qa.data.school;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.pojo.school.EducationalType;
import platform.qa.rest.RestApiClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

@Log4j2
public class SchoolRegistryRegulationsApi {

    private static final String BASE_ENDPOINT = "/edu-type/";
    private static final String SEARCH_CONTAINS_ENDPOINT = "/edu-type-contains";
    public static final String EDUCATION_TYPE_NAME = "name";
    public static final String CREATED_ID_TYPE_EDUCATION = "id";

    private final Service serviceToDataFactory;
    private final SignatureSteps signatureSteps;


    public SchoolRegistryRegulationsApi(Service dataFactory, Service digitalSignOps, List<Redis> signatureRedis) {
        this.serviceToDataFactory = dataFactory;
        this.signatureSteps = new SignatureSteps(dataFactory, digitalSignOps, signatureRedis);
    }

    public List<EducationalType> getAllEducationalType() {
        log.info("Отримання типів закладів за назвою");
        return getEducationsType(Collections.EMPTY_MAP);
    }

    public List<EducationalType> getAllEducationalTypeByName(String name) {
        log.info("Отримання всіх освітніх типів закладів за назвою {}", name);
        return getEducationsType(Map.of(EDUCATION_TYPE_NAME, name));
    }

    public EducationalType createEducationalType(EducationalType nameEducationalType) {
        log.info("Отримання всіх освітніх типів закладів за типом {}", nameEducationalType);
        String signatureId = this.signatureSteps.signRequest(nameEducationalType);
        String eduTypeId = new RestApiClient(serviceToDataFactory, signatureId).post(nameEducationalType,
                serviceToDataFactory.getUrl() + BASE_ENDPOINT).body().jsonPath().get(CREATED_ID_TYPE_EDUCATION);

        return EducationalType.builder()
                .eduTypeId(eduTypeId)
                .name(nameEducationalType.getName()).build();
    }

    public void deleteAllEducationalType() {
        log.info("Видалення всіх освітніх типів закладів");
        getAllEducationalType().stream()
                .map(EducationalType::getEduTypeId)
                .forEach(this::deleteEducationalTypeById);
    }

    public void deleteAllEducationalTypeByName(@NonNull String nameEducational) {
        log.info("Видалення всіх освітніх типів закладів які мають назву {}", nameEducational);
        getAllEducationalType().stream()
                .filter(el -> el.getName() != null && el.getName().equals(nameEducational))
                .map(EducationalType::getEduTypeId)
                .forEach(this::deleteEducationalTypeById);
    }

    public void deleteEducationalTypeById(@NonNull String eduTypeId) {
        log.info("Видалення освітнього закладу з id: {}", eduTypeId);
        String signatureId = this.signatureSteps.signDeleteRequest(eduTypeId);
        new RestApiClient(serviceToDataFactory, signatureId).delete(eduTypeId,
                serviceToDataFactory.getUrl() + BASE_ENDPOINT);
    }

    private List<EducationalType> getEducationsType(@NonNull Map<String, String> params) {
        return new RestApiClient(serviceToDataFactory)
                .sendGetWithParams(serviceToDataFactory.getUrl() + SEARCH_CONTAINS_ENDPOINT, params)
                .extract().body().as(new TypeReference<List<EducationalType>>() {
                }.getType());
    }

}
