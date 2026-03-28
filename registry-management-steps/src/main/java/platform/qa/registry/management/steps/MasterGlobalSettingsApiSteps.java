package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.GET_GLOBAL_SETTINGS;

import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.ErrorResponse;
import platform.qa.registry.management.dto.response.GlobalSettings;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class MasterGlobalSettingsApiSteps extends BaseStep {

    public MasterGlobalSettingsApiSteps(Service service) {
        super(service);
    }

    public GlobalSettings getGlobalSettings() {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(GET_GLOBAL_SETTINGS.getUrl(),
                        null,
                        new TypeReference<GlobalSettings>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public ErrorResponse getMasterGlobalSettingsUnauthorized() {
        return new RestClientProxy(service)
                .negativeRequest()
                .get(GET_GLOBAL_SETTINGS.getUrl(),
                        null,
                        new TypeReference<ErrorResponse>() {
                        }.getType(),
                        HttpStatus.SC_UNAUTHORIZED
                );
    }
}
