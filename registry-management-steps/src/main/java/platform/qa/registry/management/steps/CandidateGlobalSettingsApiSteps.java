package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.GET_GLOBAL_SETTINGS_VERSION_CANDIDATE;
import static platform.qa.registry.management.enumeration.Urls.UPDATE_GLOBAL_SETTINGS_VERSION_CANDIDATE;

import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.ErrorResponse;
import platform.qa.registry.management.dto.response.GlobalSettings;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class CandidateGlobalSettingsApiSteps extends BaseStep {

    public CandidateGlobalSettingsApiSteps(Service service) {
        super(service);
    }

    public GlobalSettings getGlobalSettings(String versionId) {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(GET_GLOBAL_SETTINGS_VERSION_CANDIDATE.getUrl().replace(ID, versionId),
                        null,
                        new TypeReference<GlobalSettings>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public GlobalSettings updateGlobalSettings(String versionId, GlobalSettings request) {
        return new RestClientProxy(service)
                .positiveRequest()
                .put(UPDATE_GLOBAL_SETTINGS_VERSION_CANDIDATE.getUrl().replace(ID, versionId),
                        null,
                        request,
                        new TypeReference<GlobalSettings>() {
                        }.getType(),
                        HttpStatus.SC_OK,
                        null
                );
    }

    public ErrorResponse getGlobalSettingsUnauthorized(String versionId) {
        return new RestClientProxy(service)
                .negativeRequest()
                .get(GET_GLOBAL_SETTINGS_VERSION_CANDIDATE.getUrl().replace(ID, versionId),
                        null,
                        new TypeReference<ErrorResponse>() {
                        }.getType(),
                        HttpStatus.SC_UNAUTHORIZED
                );
    }
}
