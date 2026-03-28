package platform.qa.registry.management.steps;

import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.MasterVersionInfoResponse;
import platform.qa.registry.management.enumeration.Urls;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;


public class MasterVersionApiSteps extends BaseStep {

    public MasterVersionApiSteps(Service service) {
        super(service);
    }

    public MasterVersionInfoResponse getMasterVersionInfo() {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(Urls.GET_MASTER_VERSIONS.getUrl(),
                        null,
                        new TypeReference<MasterVersionInfoResponse>() {}.getType(),
                        HttpStatus.SC_OK
                );
    }
}
