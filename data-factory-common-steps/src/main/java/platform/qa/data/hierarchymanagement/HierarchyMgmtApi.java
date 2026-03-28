package platform.qa.data.hierarchymanagement;

import lombok.extern.log4j.Log4j2;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.pojo.hierarchymanagement.DocumentRegistration;
import platform.qa.pojo.hierarchymanagement.Unit;
import platform.qa.rest.RestApiClient;

import java.util.List;

@Log4j2
public class HierarchyMgmtApi {

    private final String UNIT_URL = "unit/";
    private final String DOCUMENT_REGISTRATION_URL = "document-registration/";

    private SignatureSteps signatureSteps;
    private Service dataFactory;

    public HierarchyMgmtApi(Service dataFactory, Service digitalSignOps, List<Redis> signatureRedis) {
        this.dataFactory = dataFactory;
        signatureSteps = new SignatureSteps(dataFactory, digitalSignOps, signatureRedis);
    }

    /**
     * Returns a `Unit` object for the given unit ID.
     *
     * @param unitId the ID of the unit to retrieve
     * @return a `Unit` object representing the requested unit
     */
    public Unit getUnit(String unitId) {
        log.info("Отримання інформації по підрозділу за unitId: {}", unitId);
        return new RestApiClient(dataFactory)
                .get(unitId, UNIT_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(Unit.class);
    }

    /**
     * Creates a new unit and checks for its presence in the main database.
     *
     * @param unit object containing details of the unit to be created
     * @return id of the newly created unit
     */
    public String createUnit(Unit unit) {
        log.info("Створення підрозділу та перевірка наявності у головній БД: {}", unit);
        String id = signatureSteps.signRequest(unit);

        return new RestApiClient(dataFactory, id)
                .post(unit, UNIT_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .get("id");
    }

    /**
     * Deletes information about a unit with the given ID.
     *
     * @param unitId the ID of the unit to delete
     */
    public void deleteUnit(String unitId) {
        log.info("Видалення інформації по підрозділу за unitId: {}", unitId);
        String signatureId = signatureSteps.signDeleteRequest(unitId);

        new RestApiClient(dataFactory, signatureId).delete(unitId, UNIT_URL);
    }

    /**
     * Retrieves information about a registered document with the given ID.
     *
     * @param id the ID of the registered document to retrieve information for
     * @return the DocumentRegistration object containing information about the registered document
     */
    public DocumentRegistration getDocumentRegistration(String id) {
        log.info("Отримання інформації по зареєстрованому документу за id: {}", id);
        return new RestApiClient(dataFactory)
                .get(id, DOCUMENT_REGISTRATION_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(DocumentRegistration.class);
    }

    /**
     * Creates a new document registration and verifies its existence in the main database.
     *
     * @param doc the DocumentRegistration object containing information about the document to create
     * @return the ID of the newly created document registration
     */
    public String createDocumentRegistration(DocumentRegistration doc) {
        log.info("Створення документу та перевірка наявності у головній БД: {}", doc);
        String id = signatureSteps.signRequest(doc);

        return new RestApiClient(dataFactory, id)
                .post(doc, DOCUMENT_REGISTRATION_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .get("id");
    }

    /**
     * Deletes information about a registered document with the given ID.
     *
     * @param id the ID of the registered document to delete information for
     */
    public void deleteDocumentRegistration(String id) {
        log.info("Видалення інформації по документу за id: {}", id);
        String signatureId = signatureSteps.signDeleteRequest(id);

        new RestApiClient(dataFactory, signatureId).delete(id, DOCUMENT_REGISTRATION_URL);
    }
}
