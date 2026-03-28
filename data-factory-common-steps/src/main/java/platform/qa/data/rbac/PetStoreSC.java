package platform.qa.data.rbac;

import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.pojo.petstore.sc.PetStoreByName;
import platform.qa.rest.RestApiClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.reflect.TypeToken;

@Log4j2
public class PetStoreSC {
    private final String PET_STORE_BY_NAME = "rbac-pet-store-by-name/";
    private final Service service;

    public PetStoreSC(Service service) {
        this.service = service;
    }

    public List<PetStoreByName> getPetStoresByName(String petStoreName) {
        log.info("Запит до pet_store_by_name за name: {}", petStoreName);
        Map<String, String> body = new HashMap<>();
        body.put("name", String.valueOf(petStoreName));

        return new RestApiClient(service)
                .sendGetWithParams(PET_STORE_BY_NAME, body)
                .extract()
                .as(new TypeToken<List<PetStoreByName>>() {}.getType());
    }

    public Response getPetStoresByNameNegativePath(String petStoreName) {
        log.info("Запит з отриманням помилки до pet_store_by_name за name: {}", petStoreName);
        Map<String, String> body = new HashMap<>();
        body.put("name", String.valueOf(petStoreName));

        log.info("Отримання 403 помилки при виконанні запиту критерія пошуку");
        return new RestApiClient(service).getWithParams(PET_STORE_BY_NAME, body);
    }

}
