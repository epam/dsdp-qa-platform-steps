package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.CREATE_BP_GROUPS_FOR_SPECIFIC_VERSION;
import static platform.qa.registry.management.enumeration.Urls.GET_BP_GROUPS_FOR_SPECIFIC_VERSION;

import io.restassured.http.ContentType;
import platform.qa.entities.Service;
import platform.qa.registry.management.dto.grouping.BpGrouping;
import platform.qa.registry.management.dto.response.BusinessProcessGroups;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class CandidateBpGroupingApiSteps extends BaseStep {

    public CandidateBpGroupingApiSteps(Service service) {
        super(service);
    }

    public String createBusinessProcessGroup(String version, BpGrouping requestBody) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.JSON)
                .post(CREATE_BP_GROUPS_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version),
                        null,
                        requestBody,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public BusinessProcessGroups getBusinessProcessGroups(String version) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.JSON)
                .get(GET_BP_GROUPS_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version),
                        null,
                        new TypeReference<BusinessProcessGroups>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

}
