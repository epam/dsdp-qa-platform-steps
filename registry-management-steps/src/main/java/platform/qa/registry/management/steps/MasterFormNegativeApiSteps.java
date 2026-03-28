package platform.qa.registry.management.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpStatus;

import io.restassured.http.ContentType;
import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.ErrorBody;
import platform.qa.registry.management.dto.response.ErrorResponse;
import platform.qa.registry.management.dto.response.EntityInfo;
import platform.qa.registry.management.enumeration.Urls;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import java.util.List;

public class MasterFormNegativeApiSteps extends BaseStep {
    public MasterFormNegativeApiSteps(Service service) {
        super(service);
    }

    public ErrorResponse getMasterFormUnauthorized(String formName) {
        return new RestClientProxy(service)
                .negativeRequest()
                .get(Urls.GET_FORM_CONTENT_FOR_MASTER_VERSION.getUrl().replace(FORM_NAME, formName),
                        null,
                        new TypeReference<ErrorResponse>() {
                        }.getType(),
                        HttpStatus.SC_UNAUTHORIZED
                );
    }

    public ErrorBody getMasterFormNotFound(String formName) {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(Urls.GET_FORM_CONTENT_FOR_MASTER_VERSION.getUrl().replace(FORM_NAME, formName),
                        null,
                        new TypeReference<ErrorBody>() {
                        }.getType(),
                        HttpStatus.SC_NOT_FOUND
                );
    }

    public ErrorResponse getMasterFormsUnauthorized() {
        return new RestClientProxy(service)
                .negativeRequest()
                .get(Urls.GET_FORM_LIST_FOR_MASTER_VERSION.getUrl(),
                        null,
                        new TypeReference<ErrorResponse>() {}.getType(),
                        HttpStatus.SC_UNAUTHORIZED
                );
    }

    public ErrorResponse postMasterFormUnauthorized(String formName) {
        return new RestClientProxy(service)
                .negativeRequest(ContentType.JSON)
                .post(
                        Urls.CREATE_FORM_FOR_MASTER_VERSION.getUrl().replace(FORM_NAME, formName),
                        null,
                        "",
                        new TypeReference<ErrorResponse>() {}.getType(),
                        HttpStatus.SC_UNAUTHORIZED
                );
    }
}
