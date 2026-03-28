package platform.qa.data.rbac;

import lombok.extern.log4j.Log4j2;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.pojo.petstore.PetStore;
import platform.qa.rest.RestApiClient;

import java.util.List;

@Log4j2
public class PetStoreApi {
    private final String PET_STORE_URL = "pet-store/";
    private SignatureSteps signatureSteps;
    private Service dataFactory;

    public PetStoreApi(Service dataFactory, Service digitalSignOps, List<Redis> signatureRedis) {
        this.dataFactory = dataFactory;
        signatureSteps = new SignatureSteps(dataFactory, digitalSignOps, signatureRedis);
    }

    public PetStore getPetStoreById(String petStoreId) {
        log.info("Отримання інформації по сутності у таблиці pet_store за id: {}", petStoreId);
        return new RestApiClient(dataFactory)
                .get(petStoreId, PET_STORE_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(PetStore.class);
    }

    public String createPetStore(PetStore petStore) {
        log.info("Створення сутності у таблиці pet_store: {}", petStore);
        String id = signatureSteps.signRequest(petStore);

        return new RestApiClient(dataFactory, id)
                .post(petStore, PET_STORE_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .get("id");
    }

    public void updatePetStoreById(String petStoreId, PetStore petStoreUpdates) {
        log.info("Оновлення сутності з таблиці pet_store за id = {}", petStoreId);
        String id = signatureSteps.signRequest(petStoreUpdates);

        new RestApiClient(dataFactory, id).put(petStoreId, petStoreUpdates, PET_STORE_URL);
    }

    public void deletePetStoreById(String petStoreId) {
        log.info("Видалення сутності з таблиці pet store за id: {}", petStoreId);
        String signatureId = signatureSteps.signDeleteRequest(petStoreId);

        new RestApiClient(dataFactory, signatureId).delete(petStoreId, PET_STORE_URL);
    }

}
