package platform.qa.data.hierarchymanagement;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.pojo.hierarchymanagement.DocumentRegistration;
import platform.qa.pojo.hierarchymanagement.Unit;
import platform.qa.rest.RestApiClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

@Log4j2
public class HierarchyMgmtSC {

    private final String FIND_ALL_UNITS = "/find-all-units/";
    private final String DOCUMENT_REGISTRATION_COUNT_BY_UNIT_ID = "/document-registration-count-by-unit-id/";
    private final String UNITS_COUNT_BY_STRUCTURE_CODE = "/units-count-by-structure-code/";
    private final String FIND_ALL_DOCUMENTS = "/find-all-documents/";

    private final Service service;

    public HierarchyMgmtSC(Service service) {
        this.service = service;
    }

    public List<Unit> getAllUnits() {
        log.info("Отримання інформації по переліку підрозділів з таблиці unit");
        return getUnits(Collections.EMPTY_MAP);
    }

    public List<Unit> getUnits(@NonNull Map<String, String> queryParams) {
        return new RestApiClient(service)
                .sendGetWithParams(service.getUrl() + FIND_ALL_UNITS, queryParams)
                .extract().body().as(new TypeReference<List<Unit>>() {
                }.getType());
    }

    public List<DocumentRegistration> getAllDocuments() {
        log.info("Отримання інформації по переліку документів із таблиці document_registration");
        return getDocuments(Collections.EMPTY_MAP);
    }

    public List<DocumentRegistration> getDocuments(@NonNull Map<String, String> queryParams) {
        return new RestApiClient(service)
                .sendGetWithParams(service.getUrl() + FIND_ALL_DOCUMENTS, queryParams)
                .extract().body().as(new TypeReference<List<DocumentRegistration>>() {
                }.getType());
    }
}

