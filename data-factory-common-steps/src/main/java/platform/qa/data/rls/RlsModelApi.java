package platform.qa.data.rls;

import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.pojo.rls.RlsModel;
import platform.qa.rest.RestApiClient;

import java.util.List;

/**
 * Api to work with rls_model
 */
@Log4j2
public class RlsModelApi {
    private final String RLS_MODEL_URL = "rls-model/";
    private SignatureSteps signatureSteps;
    private Service dataFactory;

    public RlsModelApi(Service dataFactory, Service digitalSignOps, List<Redis> signatureRedis) {
        this.dataFactory = dataFactory;
        signatureSteps = new SignatureSteps(dataFactory, digitalSignOps, signatureRedis);
    }

    public String createRlsModel(RlsModel rlsModel) {
        log.info("Створення сутності у таблиці rls_model {}", rlsModel);
        String id = signatureSteps.signRequest(rlsModel);

        String rlsModelId = new RestApiClient(dataFactory, id).post(rlsModel, RLS_MODEL_URL)
                .then().statusCode(201).extract().jsonPath().getString("id");
        rlsModel.setRlsModelId(rlsModelId);
        return rlsModelId;
    }

    public Response createRlsModelForNegativePath(RlsModel rlsModel) {
        log.info("Створення сутності з 403 помилкою у таблиці rls_model {}", rlsModel);
        String id = signatureSteps.signRequest(rlsModel);

        return new RestApiClient(dataFactory, id).postWithError(rlsModel, RLS_MODEL_URL);
    }

    public RlsModel getRlsModelById(String rlsModelId) {
        log.info("Отримання сутності з таблиці rls_model за id = {}", rlsModelId);
        return new RestApiClient(dataFactory).get(rlsModelId, RLS_MODEL_URL)
                .then().statusCode(200).extract().as(RlsModel.class);
    }

    public Response getRlsModelByIdForNegativePath(String rlsModelId) {
        log.info("Отримання сутності з 403 помилкою з таблиці rls_model за id = {}", rlsModelId);
        return new RestApiClient(dataFactory).getWithError(rlsModelId, RLS_MODEL_URL);
    }

    public void updateRlsModelById(String rlsModelId, RlsModel rlsModel) {
        log.info("Оновлення сутності з таблиці rls_model за id = {}", rlsModelId);
        String id = signatureSteps.signRequest(rlsModel);

        new RestApiClient(dataFactory, id).put(rlsModelId, rlsModel, RLS_MODEL_URL);
    }

    public Response updateRlsModelByIdForNegativePath(String rlsModelId, RlsModel rlsModel) {
        log.info("Оновлення сутності з 403 помилкою з таблиці rls_model за id = {}", rlsModelId);
        String id = signatureSteps.signRequest(rlsModel);

        return new RestApiClient(dataFactory, id).putWithError(rlsModelId, rlsModel, RLS_MODEL_URL);
    }

    public void deleteRlsModelById(String rlsModelId) {
        log.info("Видалення сутності з таблиці rls_model за id = {}", rlsModelId);
        String id = signatureSteps.signDeleteRequest(rlsModelId);

        new RestApiClient(dataFactory, id).delete(rlsModelId, RLS_MODEL_URL);
    }
}
