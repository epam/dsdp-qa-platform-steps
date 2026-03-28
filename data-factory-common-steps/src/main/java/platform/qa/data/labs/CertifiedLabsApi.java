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

package platform.qa.data.labs;

import lombok.extern.log4j.Log4j2;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.pojo.labs.Laboratory;
import platform.qa.pojo.labs.Registration;
import platform.qa.pojo.labs.Research;
import platform.qa.pojo.labs.Staff;
import platform.qa.pojo.labs.searchConditions.StaffStatusSearch;
import platform.qa.rest.RestApiClient;

import java.util.List;

@Log4j2
public class CertifiedLabsApi {

    private final String LABORATORY_URL = "laboratory/";
    private final String REGISTRATION_URL = "registration/";
    private final String STAFF_URL = "staff/";
    private final String RESEARCH_URL = "research/";
    private final String STAFF_STATUS_URL = "staff-status/";
    private SignatureSteps signatureSteps;
    private Service dataFactory;

    public CertifiedLabsApi(Service dataFactory, Service digitalSignOps, List<Redis> signatureRedis) {
        this.dataFactory = dataFactory;
        signatureSteps = new SignatureSteps(dataFactory, digitalSignOps, signatureRedis);
    }

    public Laboratory getToLaboratory(String laboratoryId) {
        log.info("GET laboratory by laboratoryId");
        return new RestApiClient(dataFactory)
                .get(laboratoryId, LABORATORY_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(Laboratory.class);
    }

    public Staff getToStaff(String staffId) {
        log.info("GET staff by staffId");
        return new RestApiClient(dataFactory)
                .get(staffId, STAFF_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(Staff.class);
    }

    public String createLaboratory(Laboratory laboratory) {
        log.info("Створення лабораторії: {}", laboratory);
        String id = signatureSteps.signRequest(laboratory);

        return new RestApiClient(dataFactory, id)
                .post(laboratory, LABORATORY_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .get("id");
    }


    public String createLaboratory(Laboratory laboratory, String businessProcessId) {
        log.info("Створення лабораторії {} для БП {}", laboratory, businessProcessId);
        String id = signatureSteps.signRequest(laboratory);

        return new RestApiClient(dataFactory, id, businessProcessId)
                .post(laboratory, LABORATORY_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .get("id");
    }

    public String register(Registration registration) {
        log.info("Виклик API реєстрації");
        String id = signatureSteps.signRequest(registration);

        return new RestApiClient(dataFactory, id)
                .post(registration, REGISTRATION_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .get("id");
    }

    public void deleteLaboratory(String id) {
        log.info("Виклик API видалення лабораторії");
        String signatureId = signatureSteps.signDeleteRequest(id);

        new RestApiClient(dataFactory, signatureId).delete(id, LABORATORY_URL);
    }

    public void deleteRegistration(String id) {
        log.info("Виклик API видалення реєстрації");
        String signatureId = signatureSteps.signDeleteRequest(id);

        new RestApiClient(dataFactory, signatureId).delete(id, REGISTRATION_URL);
    }

    public String createStaff(Staff staff) {
        log.info("Виклик API створення персоналу");
        String id = signatureSteps.signRequest(staff);

        return new RestApiClient(dataFactory, id).post(staff, STAFF_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .get("id");
    }

    public String createStaff(Staff staff, String businessProcessId) {
        log.info("Виклик API створення персоналу для БП {}", businessProcessId);
        String id = signatureSteps.signRequest(staff);

        return new RestApiClient(dataFactory, id, businessProcessId).post(staff, STAFF_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .get("id");
    }

    public void deleteStaff(String id) {
        log.info("Виклик API видалення персоналу");
        String signatureId = signatureSteps.signDeleteRequest(id);

        new RestApiClient(dataFactory, signatureId).delete(id, STAFF_URL);
    }

    public Research getResearchById(String id) {
        log.info("Виклик API отримання research за id");
        return new RestApiClient(dataFactory)
                .get(id, RESEARCH_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(Research.class);
    }

    public StaffStatusSearch getStaffStatusById(String id) {
        log.info("Виклик API отримання staff status за id");
        return new RestApiClient(dataFactory)
                .get(id, STAFF_STATUS_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(StaffStatusSearch.class);
    }
}