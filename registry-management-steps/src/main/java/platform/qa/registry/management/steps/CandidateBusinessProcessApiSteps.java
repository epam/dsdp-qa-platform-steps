package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.CREATE_BP_FOR_SPECIFIC_VERSION;
import static platform.qa.registry.management.enumeration.Urls.DELETE_BP_FOR_SPECIFIC_VERSION;
import static platform.qa.registry.management.enumeration.Urls.GET_BP_CONTENT_FOR_SPECIFIC_VERSION;
import static platform.qa.registry.management.enumeration.Urls.GET_BP_LIST_FOR_SPECIFIC_VERSION;
import static platform.qa.registry.management.enumeration.Urls.UPDATE_BP_FOR_SPECIFIC_VERSION;

import io.restassured.http.ContentType;
import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.EntityInfo;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import java.util.List;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class CandidateBusinessProcessApiSteps extends BaseStep {

    public CandidateBusinessProcessApiSteps(Service service) {
        super(service);
    }

    public List<EntityInfo> getBpList(String version) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .get(GET_BP_LIST_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version),
                        null,
                        new TypeReference<List<EntityInfo>>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String getBusinessProcessContent(String version, String bpName) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .get(GET_BP_CONTENT_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        null,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String createBusinessProcess(String version, String content, String bpName) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .post(CREATE_BP_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        null,
                        content,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_CREATED
                );
    }

    public String updateBusinessProcess(String version, String content, String bpName) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .put(UPDATE_BP_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        null,
                        content,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK,
                        null
                );
    }

    public void deleteBusinessProcess(String version, String bpName) {
        new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .delete(DELETE_BP_FOR_SPECIFIC_VERSION.getUrl().replace(ID, version).replace(BP_NAME, bpName),
                        HttpStatus.SC_NO_CONTENT, null
                );
    }

}
