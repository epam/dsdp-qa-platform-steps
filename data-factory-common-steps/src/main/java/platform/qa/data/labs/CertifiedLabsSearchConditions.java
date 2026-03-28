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
import platform.qa.entities.Service;
import platform.qa.pojo.labs.ApplicationType;
import platform.qa.pojo.labs.Factor;
import platform.qa.pojo.labs.Koatuu;
import platform.qa.pojo.labs.Kopfg;
import platform.qa.pojo.labs.Laboratory;
import platform.qa.pojo.labs.Ownership;
import platform.qa.pojo.labs.RefusalReason;
import platform.qa.pojo.labs.Research;
import platform.qa.pojo.labs.SolutionType;
import platform.qa.pojo.labs.searchConditions.KoatuuSearch;
import platform.qa.pojo.labs.searchConditions.LaboratorySearch;
import platform.qa.pojo.labs.searchConditions.LaboratorySolutionSearch;
import platform.qa.pojo.labs.searchConditions.OwnershipSearchTypeBetween;
import platform.qa.pojo.labs.searchConditions.RegistrationSearch;
import platform.qa.pojo.labs.searchConditions.SearchKoatuuNp;
import platform.qa.pojo.labs.searchConditions.StaffCsvSearch;
import platform.qa.pojo.labs.searchConditions.StaffSearch;
import platform.qa.pojo.labs.searchConditions.StaffSearchTypeIn;
import platform.qa.pojo.labs.searchConditions.StaffSearchTypeNotIn;
import platform.qa.pojo.labs.searchConditions.StaffStatusSearch;
import platform.qa.rest.RestApiClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.reflect.TypeToken;

@Log4j2
public class CertifiedLabsSearchConditions {

    private final String LABORATORY_EQUAL_EDRPOU_NAME_COUNT = "laboratory-equal-edrpou-name-count"; //s20
    private final String LABORATORY_START_WITH_EDRPOU_CONTAINS_NAME = "laboratory-start-with-edrpou-contains-name"; //s1
    private final String KOATUU_OBL_CONTAINS_NAME = "koatuu-obl-contains-name"; //Область s2
    private final String KOATUU_NP_STARTS_WITH_NAME = "koatuu-np-starts-with-name-by-obl"; // s3
    private final String OWNERSHIP_CONTAINS_NAME = "ownership-contains-name"; //s4
    private final String KOPFG_CONTAINS_NAME = "kopfg-contains-name"; //s5
    private final String REFUSAL_REASON_EQUAL_CONSTANT_CODE_CONTAINS_NAME = "refusal-reason-equal-constant-code"
            + "-contains-name";
    private final String STAFF_CONTAINS_NAME = "staff-contains-name";
    private final String STAFF_EQUAL_CONSTANT_CODE = "staff-equal-constant-code";
    private final String FACTOR_LABOUR_CONTAINS_NAME = "factor-labour-contains-name";
    private final String FACTOR_CHEMICAL_OBRB_CONTAINS_NAME = "factor-chemical-obrb-contains-name";
    private final String FACTOR_CHEMICAL_ARBITRARY_CONTAINS_NAME = "factor-chemical-arbitrary-contains-name";
    private final String FACTOR_EQUAL_FACTOR_TYPE_NAME_COUNT = "factor-equal-factor-type-name-count";
    private final String FACTOR_CHEMICAL_HOST_CONTAINS_NAME = "factor-chemical-host-contains-name";
    private final String FACTOR_CHEMICAL_HYGIENE_CONTAINS_NAME = "factor-chemical-hygiene-contains-name";
    private final String FACTOR_BIO_CONTAINS_NAME = "factor-bio-contains-name";
    private final String FACTOR_PHYSICAL_CONTAINS_NAME = "factor-physical-contains-name";
    private final String APPLICATION_TYPE_EQUAL_CONSTANT_CODE = "application-type-equal-constant-code";
    private final String SOLUTION_TYPE_EQUAL_CONSTANT_CODE = "solution-type-equal-constant-code";
    private final String RESEARCH_CONTAINS_NAME = "research-contains-name";
    private final String KOATUU_EQUAL_KOATUU_ID_NAME = "koatuu-equal-koatuu-id-name";
    private final String STAFF_EQUAL_LABORATORY_ID_CONTAINS_FULL_NAME = "staff-equal-laboratory-id-contains-full-name";
    private final String STAFF_EQUAL_LABORATORY_ID_COUNT = "staff-equal-laboratory-id-count";
    private final String LABORATORY_EQUAL_SUBJECT = "laboratory-equal-subject-id/";
    private final String LAST_LABORATORY_SOLUTION = "last-laboratory-solution/";
    private final String FACTOR_CHEM_HOST_DOVIL = "factors-chem-host-dovil";
    private final String LABORATORY_EQUAL_SUBJECT_CODE_NAME = "laboratory-equal-subject-code-name/";
    private final String REGISTRATION_EQUAL_LABORATORY_ID_SOLUTION = "registration-equal-laboratory-id-solution/";
    private final String STAFF_EQUAL_LABORATORY_ID = "staff-equal-laboratory-id/";
    private final String STAFF_FIND_IN_SALARY = "staff-find-in-salary/";
    private final String STAFF_FIND_NOT_IN_SALARY = "staff-find-not-in-salary/";
    private final String OWNERSHIP_FIND_BETWEEN_CODE = "ownership-find-between-code/";


    private final Service service;

    public CertifiedLabsSearchConditions(Service service) {
        this.service = service;
    }


    public String laboratoryEqualEdrpouNameCount(String name, String edrpou) {
        log.info("Запит laboratory-equal-edrpou-name-count");

        if (name == null || edrpou == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        queryParams.put("edrpou", edrpou);


        return new RestApiClient(service).sendGetWithParams(LABORATORY_EQUAL_EDRPOU_NAME_COUNT, queryParams)
                .extract()
                .jsonPath().getString("cnt")
                .replace("]", "").replace("[", "");
    }

    public List<Laboratory> laboratoryStartWithEdrpouContainsName(String name, String edrpou) {
        log.info("Запит до ЄДРПОУ та Назва лабораторії laboratory_start_with_edrpou_contains_name");

        if (name == null || edrpou == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        queryParams.put("edrpou", edrpou);
        return new RestApiClient(service)
                .sendGetWithParams(LABORATORY_START_WITH_EDRPOU_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Laboratory>>() {
                }.getType());
    }


    public List<Koatuu> koatuuOblContainsName(String koatuuName) {
        log.info("Запит koatuu-obl-contains-name-search");

        if (koatuuName == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", koatuuName);
        return new RestApiClient(service)
                .sendGetWithParams(KOATUU_OBL_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Koatuu>>() {
                }.getType());
    }

    public List<SearchKoatuuNp> koatuuNpStartsWithName(String koatuLevel, String koatuName) {
        log.info("Запит до condition oblast koatuu-np-starts-with-name");

        if (koatuLevel == null || koatuName == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("level1", koatuLevel);
        queryParams.put("name", koatuName);
        return new RestApiClient(service)
                .sendGetWithParams(KOATUU_NP_STARTS_WITH_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<SearchKoatuuNp>>() {
                }.getType());
    }

    public List<Ownership> ownershipContainsName(String name) {
        log.info("Запит до condition oblast ownership-contains-name");

        if (name == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return new RestApiClient(service)
                .sendGetWithParams(OWNERSHIP_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Ownership>>() {
                }.getType());
    }

    public List<Kopfg> kopfgСontainsName(String kopfgName) {
        log.info("Запит до condition oblast kopfg-contains-name");

        if (kopfgName == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", kopfgName);
        return new RestApiClient(service)
                .sendGetWithParams(KOPFG_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Kopfg>>() {
                }.getType());
    }

    public List<RefusalReason> refusalReasonEqualConstantCode(String constantCode, String name) {
        log.info("Запит до refusal-reason-equal-constant-code-contains-name");

        if (constantCode == null || name == null) {
            return null;

        }
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("constantCode", constantCode);
        queryParams.put("name", name);
        return new RestApiClient(service)
                .sendGetWithParams(REFUSAL_REASON_EQUAL_CONSTANT_CODE_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<RefusalReason>>() {
                }.getType());
    }

    public List<StaffStatusSearch> staffContainsName(String name) {
        log.info("Запит до staff_contains_name");

        if (name == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return new RestApiClient(service)
                .sendGetWithParams(STAFF_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<StaffStatusSearch>>() {
                }.getType());
    }


    public List<StaffStatusSearch> staffEqualConstantCode(String constantCode) {
        log.info("Запит до staff_equal_constant_code");

        if (constantCode == null) {
            return null;

        }
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("constantCode", constantCode);
        return new RestApiClient(service)
                .sendGetWithParams(STAFF_EQUAL_CONSTANT_CODE, queryParams)
                .extract()
                .as(new TypeToken<List<StaffStatusSearch>>() {
                }.getType());
    }

    public List<Factor> factorLabourContainsName(String name) {
        log.info("Запит до factor_labour_contains_name");

        if (name == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return new RestApiClient(service)
                .sendGetWithParams(FACTOR_LABOUR_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Factor>>() {
                }.getType());
    }

    public List<Factor> factorChemicalObrbContainsName(String name) {
        log.info("Запит до factor_chemical_obrb_contains_name");

        if (name == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return new RestApiClient(service)
                .sendGetWithParams(FACTOR_CHEMICAL_OBRB_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Factor>>() {
                }.getType());
    }

    public List<Factor> factorChemicalArbitraryContainsName(String name) {
        log.info("Запит до factor_chemical_arbitrary_contains_name");

        if (name == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return new RestApiClient(service)
                .sendGetWithParams(FACTOR_CHEMICAL_ARBITRARY_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Factor>>() {
                }.getType());
    }

    public String factorEqualFactorTypeNameCount(String name) {
        log.info("Запит до factor_equal_factor_type_name_count");

        if (name == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return new RestApiClient(service).sendGetWithParams(FACTOR_EQUAL_FACTOR_TYPE_NAME_COUNT, queryParams)
                .extract()
                .jsonPath().getString("cnt")
                .replace("]", "").replace("[", "");
    }

    public List<Factor> factorChemicalHostContainsName(String name) {
        log.info("Запит до factor_chemical_host_contains_name");

        if (name == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return new RestApiClient(service)
                .sendGetWithParams(FACTOR_CHEMICAL_HOST_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Factor>>() {
                }.getType());
    }


    public List<SolutionType> solutionTypeEqualConstantCode(String constantCode) {
        log.info("Запит до solution_type_equal_constant_code");

        if (constantCode == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("constantCode", constantCode);
        return new RestApiClient(service)
                .sendGetWithParams(SOLUTION_TYPE_EQUAL_CONSTANT_CODE, queryParams)
                .extract()
                .as(new TypeToken<List<SolutionType>>() {
                }.getType());
    }

    public List<Research> researchContainsName(String researchType) {
        log.info("Запит до research-contains-name");

        if (researchType == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("researchType", researchType);
        return new RestApiClient(service)
                .sendGetWithParams(RESEARCH_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Research>>() {
                }.getType());
    }

    public List<KoatuuSearch> koatuuEqualKoatuuIdName(String koatuuId) {
        log.info("Запит до koatuu_equal_koatuu_id_name");

        if (koatuuId == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("koatuuId", koatuuId);
        return new RestApiClient(service)
                .sendGetWithParams(KOATUU_EQUAL_KOATUU_ID_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<KoatuuSearch>>() {
                }.getType());
    }

    public List<StaffSearch> staffEqualLaboratoryIdContainsFullName(String laboratoryId, String fullStaffName) {
        log.info("Запит до staff_equal_laboratory_id_contains_full_name");

        if (laboratoryId == null || fullStaffName == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("laboratoryId", laboratoryId);
        queryParams.put("fullName", fullStaffName);
        return new RestApiClient(service)
                .sendGetWithParams(STAFF_EQUAL_LABORATORY_ID_CONTAINS_FULL_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<StaffSearch>>() {
                }.getType());
    }

    public String staffEqualLaboratoryIdCount(String laboratoryId) {
        log.info("Запит до staff_equal_laboratory_id_count");

        if (laboratoryId == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("laboratoryId", laboratoryId);
        return new RestApiClient(service).sendGetWithParams(STAFF_EQUAL_LABORATORY_ID_COUNT, queryParams)
                .extract()
                .jsonPath().getString("cnt")
                .replace("]", "").replace("[", "");
    }

    public List<Factor> factorChemicalHygieneContainsName(String name) {
        log.info("Запит до factor-chemical-hygiene-contains-name");

        if (name == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return new RestApiClient(service)
                .sendGetWithParams(FACTOR_CHEMICAL_HYGIENE_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Factor>>() {
                }.getType());
    }

    public List<Factor> factorBioContainsName(String name) {
        log.info("Запит до factor-bio-contains-name");

        if (name == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return new RestApiClient(service)
                .sendGetWithParams(FACTOR_BIO_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Factor>>() {
                }.getType());
    }

    public List<Factor> factorPhysicalContainsName(String name) {
        log.info("Запит до factor-physical-contains-name");

        if (name == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        return new RestApiClient(service)
                .sendGetWithParams(FACTOR_PHYSICAL_CONTAINS_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<Factor>>() {
                }.getType());
    }

    public List<ApplicationType> applicationTypeEqualConstantCode(String constantCode) {
        log.info("Запит до application-type-equal-constant-code");

        if (constantCode == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("constantCode", constantCode);
        return new RestApiClient(service)
                .sendGetWithParams(APPLICATION_TYPE_EQUAL_CONSTANT_CODE, queryParams)
                .extract()
                .as(new TypeToken<List<ApplicationType>>() {
                }.getType());
    }

    public List<Laboratory> laboratoryEqualSubject(String subjectId) {
        log.info("Запиту до subject laboratory-equal-subject-id");

        if (subjectId == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("subjectId", subjectId);
        return new RestApiClient(service)
                .sendGetWithParams(LABORATORY_EQUAL_SUBJECT, queryParams)
                .extract()
                .as(new TypeToken<List<Laboratory>>() {
                }.getType());
    }

    public List<Factor> getFactorsChemHostDovil() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("searchConditions", "{}");
        return new RestApiClient(service)
                .sendGetWithParams(FACTOR_CHEM_HOST_DOVIL, queryParams)
                .extract()
                .as(new TypeToken<List<Factor>>() {
                }.getType());
    }

    public List<LaboratorySolutionSearch> lastLaboratorySolution(String laboratoryId) {
        log.info("Запиту до laboratory last-laboratory-solution");

        if (laboratoryId == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("laboratoryId", laboratoryId);
        return new RestApiClient(service)
                .sendGetWithParams(LAST_LABORATORY_SOLUTION, queryParams)
                .extract()
                .as(new TypeToken<List<LaboratorySolutionSearch>>() {
                }.getType());
    }

    public List<LaboratorySearch> laboratoryEqualSubjectCodeName(String subjectCode, String subjectType,
            String subjectId) {
        log.info("Запиту до laboratory laboratory-equal-subject-code-name");

        if (subjectCode == null || subjectType == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("subjectId", subjectId);
        queryParams.put("subjectCode", subjectCode);
        queryParams.put("subjectType", subjectType);
        return new RestApiClient(service)
                .sendGetWithParams(LABORATORY_EQUAL_SUBJECT_CODE_NAME, queryParams)
                .extract()
                .as(new TypeToken<List<LaboratorySearch>>() {
                }.getType());
    }

    public List<RegistrationSearch> registrationEqualLaboratoryIdSolution(String laboratoryId, String solutionCode) {
        log.info("Запиту до laboratory registration_equal_laboratory_id_solution");

        if (laboratoryId == null || solutionCode == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("laboratoryId", laboratoryId);
        queryParams.put("solutionCode", solutionCode);
        return new RestApiClient(service)
                .sendGetWithParams(REGISTRATION_EQUAL_LABORATORY_ID_SOLUTION, queryParams)
                .extract()
                .as(new TypeToken<List<RegistrationSearch>>() {
                }.getType());
    }

    public List<RegistrationSearch> registrationEqualLaboratoryIdSolutionWithOneParameters(String laboratoryId) {
        log.info("Запиту до laboratory registration_equal_laboratory_id_solution");

        if (laboratoryId == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("laboratoryId", laboratoryId);
        return new RestApiClient(service)
                .sendGetWithParams(REGISTRATION_EQUAL_LABORATORY_ID_SOLUTION, queryParams)
                .extract()
                .as(new TypeToken<List<RegistrationSearch>>() {
                }.getType());
    }

    public List<StaffCsvSearch> staffEqualLaboratoryId(String laboratoryId) {
        log.info("Запиту до staff staff_equal_laboratory_id");

        if (laboratoryId == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("laboratoryId", laboratoryId);
        return new RestApiClient(service)
                .sendGetWithParams(STAFF_EQUAL_LABORATORY_ID, queryParams)
                .extract()
                .as(new TypeToken<List<StaffCsvSearch>>() {
                }.getType());
    }

    public List<StaffSearchTypeIn> staffFindInSalary(String salary) {
        log.info("Запиту до staff staff_equal_laboratory_id");

        if (salary == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("salary", salary);
        return new RestApiClient(service)
                .sendGetWithParams(STAFF_FIND_IN_SALARY, queryParams)
                .extract()
                .as(new TypeToken<List<StaffSearchTypeIn>>() {}.getType());
    }

    public List<StaffSearchTypeNotIn> staffFindNotInSalary(String salary) {
        log.info("Запиту до staff staff_find_not_in_salary");

        if (salary == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("salary", salary);
        return new RestApiClient(service)
                .sendGetWithParams(STAFF_FIND_NOT_IN_SALARY, queryParams)
                .extract()
                .as(new TypeToken<List<StaffSearchTypeNotIn>>() {}.getType());
    }

    public List<OwnershipSearchTypeBetween> ownershipFindBetweenCode(String codeFrom, String codeTo) {
        log.info("Запиту до ownership ownership_find_between_code");
        if (codeFrom == null || codeTo == null) {
            return null;
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("codeFrom", codeFrom);
        queryParams.put("codeTo", codeTo);

        return new RestApiClient(service)
                .sendGetWithParams(OWNERSHIP_FIND_BETWEEN_CODE, queryParams)
                .extract()
                .as(new TypeToken<List<OwnershipSearchTypeBetween>>() {}.getType());
    }
}
