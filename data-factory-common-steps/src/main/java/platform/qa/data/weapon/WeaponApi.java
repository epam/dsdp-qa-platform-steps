package platform.qa.data.weapon;

import lombok.extern.log4j.Log4j2;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.pojo.weapon.Brand;
import platform.qa.pojo.weapon.Model;
import platform.qa.pojo.weapon.Type;
import platform.qa.pojo.weapon.Weapon;
import platform.qa.rest.RestApiClient;

import java.util.List;

@Log4j2
public class WeaponApi {

    private final String WEAPON_URL = "weapon/";
    private final String TYPE_URL = "type/";
    private final String MODEL_URL = "model/";
    private final String BRAND_URL = "brand/";

    private SignatureSteps signatureSteps;
    private Service dataFactory;

    public WeaponApi(Service dataFactory, Service digitalSignOps, List<Redis> signatureRedis) {
        this.dataFactory = dataFactory;
        signatureSteps = new SignatureSteps(dataFactory, digitalSignOps, signatureRedis);
    }

    public String createWeapon(Weapon weapon) {
        log.info("Create weapon in DB: {}", weapon);
        String id = signatureSteps.signRequest(weapon);

        return new RestApiClient(dataFactory, id)
                .post(weapon, WEAPON_URL).then().statusCode(201)
                .extract().jsonPath().get("id");
    }

    public Weapon getWeaponById(String id) {
        log.info("Get weapon by id {}", id);
        return new RestApiClient(dataFactory)
                .get(id, WEAPON_URL).then().statusCode(200)
                .extract().as(Weapon.class);
    }

    public Type getWeaponTypeById(String typeId) {
        log.info("Get weapon type by typeId {}", typeId);
        return new RestApiClient(dataFactory)
                .get(typeId, TYPE_URL).then().statusCode(200)
                .extract().as(Type.class);
    }

    public Model getWeaponModelById(String modelId) {
        log.info("Get weapon model by modelId {}", modelId);
        return new RestApiClient(dataFactory)
                .get(modelId, MODEL_URL).then().statusCode(200)
                .extract().as(Model.class);
    }

    public Brand getWeaponBrandById(String brandId) {
        log.info("Get weapon brand by brandId {}", brandId);
        return new RestApiClient(dataFactory)
                .get(brandId, BRAND_URL).then().statusCode(200)
                .extract().as(Brand.class);
    }
}
