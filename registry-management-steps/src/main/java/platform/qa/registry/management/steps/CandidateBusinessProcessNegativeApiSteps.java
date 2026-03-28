package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.CREATE_BP_FOR_SPECIFIC_VERSION;
import static platform.qa.registry.management.enumeration.Urls.DELETE_BP_FOR_SPECIFIC_VERSION;
import static platform.qa.registry.management.enumeration.Urls.GET_BP_CONTENT_FOR_SPECIFIC_VERSION;
import static platform.qa.registry.management.enumeration.Urls.GET_BP_LIST_FOR_SPECIFIC_VERSION;
import static platform.qa.registry.management.enumeration.Urls.UPDATE_BP_FOR_SPECIFIC_VERSION;

import io.restassured.http.ContentType;
import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.ErrorResponse;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import java.util.List;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class CandidateBusinessProcessNegativeApiSteps extends BaseStep {

    public CandidateBusinessProcessNegativeApiSteps(Service service) {
        super(service);
    }

    public ErrorResponse getBpContentUnauthorized(String version, String bpName) {
        return new RestClientProxy(service)
                .negativeRequest(ContentType.XML)
                .get(GET_BP_CONTENT_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        null,
                        new TypeReference<ErrorResponse>() {
                        }.getType(),
                        HttpStatus.SC_UNAUTHORIZED
                );
    }

    public ErrorResponse getBpContentNotMethodAllowed(String version, String bpName) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .post(
                        GET_BP_LIST_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version),
                        null,
                        bpName,
                        new TypeReference<ErrorResponse>() {}.getType(),
                        HttpStatus.SC_METHOD_NOT_ALLOWED
                );
    }

    public ErrorResponse getBpContentNotFound(String version, String bpName) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .get(GET_BP_CONTENT_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        null,
                        new TypeReference<ErrorResponse>() {
                        }.getType(),
                        HttpStatus.SC_NOT_FOUND
                );
    }

    public ErrorResponse createBpContentUnauthorized(String version, String content, String bpName) {
        return new RestClientProxy(service)
                .negativeRequest(ContentType.XML)
                .post(CREATE_BP_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        null,
                        content,
                        new TypeReference<ErrorResponse>() {
                        }.getType(),
                        HttpStatus.SC_UNAUTHORIZED
                );
    }

    public ErrorResponse updateBpContentUnauthorized(String version, String content, String bpName) {
        return new RestClientProxy(service)
                .negativeRequest(ContentType.XML)
                .put(UPDATE_BP_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        null,
                        content,
                        new TypeReference<ErrorResponse>() {
                        }.getType(),
                        HttpStatus.SC_UNAUTHORIZED,
                        null
                );
    }

    public ErrorResponse updateBpContentConflict(String version, String content, String bpName) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .put(UPDATE_BP_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        null,
                        content,
                        new TypeReference<ErrorResponse>() {
                        }.getType(),
                        HttpStatus.SC_CONFLICT,
                        null
                );
    }

    public void deleteBpContentUnauthorized(String version, String bpName) {
        new RestClientProxy(service)
                .negativeRequest(ContentType.XML)
                .delete(DELETE_BP_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        HttpStatus.SC_UNAUTHORIZED, null
                );
    }

    public void deleteBpContentNotFound(String version, String bpName) {
        new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .delete(DELETE_BP_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        HttpStatus.SC_NOT_FOUND, null
                );
    }

    public void createBusinessProcessWithInvalidXml(String version, String content, String bpName) {
        new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .post(CREATE_BP_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        null,
                        content,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_UNPROCESSABLE_ENTITY
                );
    }
}
