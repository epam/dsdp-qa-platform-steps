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

package platform.qa.data.consent;

import lombok.extern.log4j.Log4j2;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.entities.Subject;
import platform.qa.pojo.consent.PersonProfile;
import platform.qa.rest.RestApiClient;

import java.util.List;
import java.util.Map;

/**
 * Api to manipulate user personal profile
 */
@Log4j2
public class PersonalProfileApi {
    private final String PERSON_PROFILE_URL = "person-profile/";
    private final String PERSON_PROFILE_EQUAL_LAST_NAME_URL = "person-profile-equal-last-name/";
    private final String LAST_NAME = "lastName";

    private final SignatureSteps signatureSteps;
    private final Service dataFactory;

    public PersonalProfileApi(Service dataFactory, Service digitalSignOps, List<Redis> signatureRedis) {
        this.dataFactory = dataFactory;
        signatureSteps = new SignatureSteps(dataFactory, digitalSignOps, signatureRedis);
    }

    public Subject getPersonById(String personProfileId) {
        log.info("GET person by id: {}", personProfileId);
        return new RestApiClient(dataFactory)
                .get(personProfileId, PERSON_PROFILE_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(Subject.class);
    }

    public List<PersonProfile> getPersonByLastName(String lastName) {
        log.info("GET person by lastName: {}", lastName);
        return new RestApiClient(dataFactory)
                .sendGetWithParams(PERSON_PROFILE_EQUAL_LAST_NAME_URL, Map.of(LAST_NAME, lastName))
                .extract()
                .jsonPath()
                .getList("", PersonProfile.class);
    }

    public void deletePersonProfile(String personProfileId) {
        log.info("DELETE person by id: {}", personProfileId);
        String signatureId = signatureSteps.signDeleteRequest(personProfileId);

        new RestApiClient(dataFactory, signatureId).delete(personProfileId, PERSON_PROFILE_URL);
    }
}
