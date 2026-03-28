/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.data.consent;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.pojo.map.CreateKatottgMap;
import platform.qa.pojo.map.EntityAddressLocationResponse;
import platform.qa.pojo.map.EntityGeoType;
import platform.qa.pojo.map.EntityGeoTypeManyDots;
import platform.qa.pojo.map.EntityGeoTypeResponse;
import platform.qa.rest.RestApiClient;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

@Log4j2
public class EntityWithGeoTypeApi {
    private final String ENTITY_WITH_GEO_TYPE_URL = "entity-with-geo-type/";
    private final String GET_ENTITY_WITH_ADDRESS_URL = "get-entity-address-from-table-geo-type/";
    private final String CREATE_POINT_WITH_GEO_TYPE_URL = "katottg-map/";

    private final Service serviceToDataFactory;
    private final SignatureSteps signatureSteps;

    public EntityWithGeoTypeApi(Service dataFactory, Service digitalSignOps, List<Redis> signatureRedis) {
        this.serviceToDataFactory = dataFactory;
        this.signatureSteps = new SignatureSteps(dataFactory, digitalSignOps, signatureRedis);
    }

    public EntityGeoTypeResponse getLocation(String id) {
        return new RestApiClient(serviceToDataFactory, "signature")
                .get(id, ENTITY_WITH_GEO_TYPE_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(EntityGeoTypeResponse.class);
    }

    public String createLocation(EntityGeoType location) {
        log.info("Створення координат: {}", location);

        String signatureId = this.signatureSteps.signRequest(location);
        return new RestApiClient(serviceToDataFactory, signatureId)
                .post(location, ENTITY_WITH_GEO_TYPE_URL)
                .then().statusCode(201)
                .extract().jsonPath()
                .getString("id");
    }

    public String createLocation(EntityGeoTypeManyDots location) {
        log.info("Створення координат GeoTypeManyDots: {}", location);

        String signatureId = this.signatureSteps.signRequest(location);
        return new RestApiClient(serviceToDataFactory, signatureId)
                .post(location, ENTITY_WITH_GEO_TYPE_URL)
                .then().statusCode(201)
                .extract().jsonPath()
                .getString("id");
    }

    public void updateLocation(String recordId, EntityGeoType location) {
        log.info("Update location by id: {}", recordId);
        String signatureId = this.signatureSteps.signRequest(location);

        new RestApiClient(serviceToDataFactory, signatureId).put(recordId, location, ENTITY_WITH_GEO_TYPE_URL);
    }

    public void deleteLocationById(String locationId) {
        log.info("DELETE location by id: {}", locationId);
        String signatureId = signatureSteps.signDeleteRequest(locationId);

        new RestApiClient(serviceToDataFactory, signatureId).delete(locationId, ENTITY_WITH_GEO_TYPE_URL);
    }

    public List<EntityAddressLocationResponse> getEntityAddressLocation(@NonNull Map<String, String> params) {
        return new RestApiClient(serviceToDataFactory)
                .sendGetWithParams(GET_ENTITY_WITH_ADDRESS_URL, params)
                .extract().body().as(new TypeReference<List<EntityAddressLocationResponse>>() {
                }.getType());
    }

    public String createPointOnMap(CreateKatottgMap payload) {
        String signature = signatureSteps.signRequest(payload);

        return new RestApiClient(serviceToDataFactory, signature)
                .post(payload, CREATE_POINT_WITH_GEO_TYPE_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getString("id");
    }

    public void removePointOnMap(String id) {
        String signature = signatureSteps.signDeleteRequest(id);

        new RestApiClient(serviceToDataFactory, signature)
                .delete(id, CREATE_POINT_WITH_GEO_TYPE_URL);
    }

    public String createLineOnMap(CreateKatottgMap payload) {
        return createGeometryOnMap(payload);
    }

    public String createPolygonOnMap(CreateKatottgMap payload) {
        return createGeometryOnMap(payload);
    }

    private String createGeometryOnMap(CreateKatottgMap payload) {
        String signature = signatureSteps.signRequest(payload);
        return new RestApiClient(serviceToDataFactory, signature)
                .post(payload, CREATE_POINT_WITH_GEO_TYPE_URL)
                .then()
                .statusCode(201)
                .extract().jsonPath()
                .getString("id");
    }
}
