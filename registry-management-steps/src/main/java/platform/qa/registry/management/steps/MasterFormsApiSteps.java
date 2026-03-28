package platform.qa.registry.management.steps;

import platform.qa.entities.Service;
import platform.qa.registry.management.dto.request.form.Form;
import platform.qa.registry.management.dto.response.EntityInfo;
import platform.qa.registry.management.enumeration.Urls;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;


public class MasterFormsApiSteps extends BaseStep {

    public MasterFormsApiSteps(Service service) {
        super(service);
    }

    public List<EntityInfo> getFormListFromMasterVersion() {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(Urls.GET_FORM_LIST_FOR_MASTER_VERSION.getUrl(),
                        null,
                        new TypeReference<List<EntityInfo>>() {}.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String getFormContentFromMasterVersion(String formName) {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(Urls.GET_FORM_CONTENT_FOR_MASTER_VERSION.getUrl().replace(FORM_NAME, formName),
                        null,
                        new TypeReference<String>() {}.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String createFormInMasterVersion(Form request) {
        return new RestClientProxy(service)
                .positiveRequest()
                .post(Urls.CREATE_FORM_FOR_MASTER_VERSION.getUrl().replace(FORM_NAME, request.getName()),
                        null,
                        request,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_CREATED
                );
    }

    public String updateFormInMasterVersion(Form request, Map<String,String> headers) {
        return new RestClientProxy(service)
                .positiveRequest()
                .put(Urls.UPDATE_FORM_FOR_MASTER_VERSION.getUrl().replace(FORM_NAME, request.getName()),
                        null,
                        request,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK,
                        headers
                );
    }

    public void deleteFormInMasterVersion(String formName, Map<String,String> headers) {
        new RestClientProxy(service)
                .positiveRequest()
                .delete(Urls.DELETE_FORM_FOR_MASTER_VERSION.getUrl().replace(FORM_NAME, formName),
                        HttpStatus.SC_NO_CONTENT, headers
                );
    }
}