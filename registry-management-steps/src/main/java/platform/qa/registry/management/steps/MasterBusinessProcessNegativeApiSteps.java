package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.GET_BP_CONTENT_FROM_MASTER_VERSION;
import static platform.qa.registry.management.enumeration.Urls.GET_BP_LIST_FROM_MATER_VERSION;

import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.ErrorResponse;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import java.util.List;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class MasterBusinessProcessNegativeApiSteps extends BaseStep {

    public MasterBusinessProcessNegativeApiSteps(Service service) {
        super(service);
    }

    public ErrorResponse getBusinessProcessContentUnauthorized(String bpName) {
        return new RestClientProxy(service)
                .negativeRequest()
                .get(GET_BP_CONTENT_FROM_MASTER_VERSION.getUrl().replace(BP_NAME, bpName),
                        null,
                        new TypeReference<ErrorResponse>() {
                        }.getType(),
                        HttpStatus.SC_UNAUTHORIZED
                );
    }

    public ErrorResponse getBusinessProcessContentNotFound(String bpName) {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(GET_BP_CONTENT_FROM_MASTER_VERSION.getUrl().replace(BP_NAME, bpName),
                        null,
                        new TypeReference<ErrorResponse>() {
                        }.getType(),
                        HttpStatus.SC_NOT_FOUND
                );
    }

    public List<ErrorResponse> getListBusinessProcessContentUnauthorized() {
        return new RestClientProxy(service)
                .negativeRequest()
                .get(GET_BP_LIST_FROM_MATER_VERSION.getUrl(),
                        null,
                        new TypeReference<List<ErrorResponse>>() {}.getType(),
                        HttpStatus.SC_UNAUTHORIZED
                );
    }

}
