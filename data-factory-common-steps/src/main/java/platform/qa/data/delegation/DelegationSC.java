package platform.qa.data.delegation;

import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.pojo.delegation.DelegationAuthorizedPerson;
import platform.qa.pojo.delegation.DelegationLicense;
import platform.qa.pojo.delegation.DelegationOrganization;
import platform.qa.rest.RestApiClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.reflect.TypeToken;

@Log4j2
public class DelegationSC {

    private static final String AUTH_PERSONS_BY_EDRPOU = "/get-delegation-authorized-persons-by-edrpou/";
    private static final String AUTH_PERSONS_EXISTS = "/is-delegation-authorized-person-exists/";
    private static final String ALL_ACTIVE_LICENSES = "/get-all-active-licenses-for-organization/";
    private static final String ORGANIZATION_BY_EDRPOU = "/get-delegation-organization-by-edrpou/";

    private final Service service;

    public DelegationSC(Service service) {
        this.service = service;
    }

    public List<DelegationAuthorizedPerson> getAuthPersonsByEdrpou(String organizationEdrpou) {
        log.info("Запит до delegation_authorized_person");
        if (organizationEdrpou == null) return null;

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("organizationEdrpou", organizationEdrpou);
        return new RestApiClient(service)
                .sendGetWithParams(AUTH_PERSONS_BY_EDRPOU, queryParams)
                .extract()
                .as(new TypeToken<List<DelegationAuthorizedPerson>>() {
                }.getType());
    }


    public String isDelegationAuthPersonExist(String organizationEdrpou, String edrpou, String drfo) {
        log.info("Запит до is-delegation-authorized-person-exists");
        if (organizationEdrpou == null || edrpou == null || drfo == null) return null;

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("organizationEdrpou", organizationEdrpou);
        queryParams.put("edrpou", edrpou);
        queryParams.put("drfo", drfo);


        return new RestApiClient(service).sendGetWithParams(AUTH_PERSONS_EXISTS, queryParams)
                .extract()
                .jsonPath().getString("cnt")
                .replace("]", "").replace("[", "");
    }

    public List<DelegationLicense> getAllActiveLicensesForOrganization(String organizationEdrpou) {
        log.info("Запит до get-all-active-licenses-for-organization");
        if (organizationEdrpou == null) return null;

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("organizationEdrpou", organizationEdrpou);
        return new RestApiClient(service)
                .sendGetWithParams(ALL_ACTIVE_LICENSES, queryParams)
                .extract()
                .as(new TypeToken<List<DelegationLicense>>() {
                }.getType());
    }

    public List<DelegationOrganization> getDelegationOrganizationByEdprou(String organizationEdrpou) {
        log.info("Запит до get-delegation-organization-by-edrpou");
        if (organizationEdrpou == null) return null;

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("edrpou", organizationEdrpou);
        queryParams.put("limit", "100");
        return new RestApiClient(service)
                .sendGetWithParams(ORGANIZATION_BY_EDRPOU, queryParams)
                .extract()
                .as(new TypeToken<List<DelegationOrganization>>() {
                }.getType());
    }
}
