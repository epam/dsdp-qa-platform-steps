package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.GET_BP_GROUPS_FROM_MASTER_VERSION;

import io.restassured.http.ContentType;
import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.BusinessProcessGroups;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class MasterBpGroupingApiSteps extends BaseStep {

    public MasterBpGroupingApiSteps(Service service) {
        super(service);
    }

    public BusinessProcessGroups getBpGroupsFromMasterVersion() {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.JSON)
                .get(GET_BP_GROUPS_FROM_MASTER_VERSION.getUrl(),
                        null,
                        new TypeReference<BusinessProcessGroups>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }
}
