package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.BP_GROUPING_ROLLBACK_FOR_VERSION_CANDIDATE;
import static platform.qa.registry.management.enumeration.Urls.BP_ROLLBACK_FOR_VERSION_CANDIDATE;
import static platform.qa.registry.management.enumeration.Urls.FORM_ROLLBACK_FOR_VERSION_CANDIDATE;
import static platform.qa.registry.management.enumeration.Urls.TABLES_ROLLBACK_FOR_VERSION_CANDIDATE;
import static platform.qa.registry.management.enumeration.Urls.TRANSLATION_ROLLBACK_FOR_VERSION_CANDIDATE;

import io.restassured.http.ContentType;
import platform.qa.entities.Service;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class RollbackApiSteps extends BaseStep {

    public RollbackApiSteps(Service service) {
        super(service);
    }

    public String rollbackBusinessProcess(String bpName, String versionCandidateId) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.JSON)
                .post(BP_ROLLBACK_FOR_VERSION_CANDIDATE.getUrl().replace(ID, versionCandidateId).replace(BP_NAME,
                                bpName),
                        null,
                        StringUtils.EMPTY,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String rollbackForm(String formName, String versionCandidateId) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.JSON)
                .post(FORM_ROLLBACK_FOR_VERSION_CANDIDATE.getUrl().replace(ID, versionCandidateId).replace(FORM_NAME,
                                formName),
                        null,
                        StringUtils.EMPTY,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String rollbackBpGrouping(String versionCandidateId) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.JSON)
                .post(BP_GROUPING_ROLLBACK_FOR_VERSION_CANDIDATE.getUrl().replace(ID, versionCandidateId),
                        null,
                        StringUtils.EMPTY,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String rollbackDataModel(String versionCandidateId) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.JSON)
                .post(TABLES_ROLLBACK_FOR_VERSION_CANDIDATE.getUrl().replace(ID, versionCandidateId),
                        null,
                        StringUtils.EMPTY,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String rollbackTranslation(String languageName, String versionCandidateId) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.JSON)
                .post(TRANSLATION_ROLLBACK_FOR_VERSION_CANDIDATE.getUrl().replace(ID, versionCandidateId).replace(LANGUAGE_NAME,
                                languageName),
                        null,
                        StringUtils.EMPTY,
                        new TypeReference<String>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }
}
