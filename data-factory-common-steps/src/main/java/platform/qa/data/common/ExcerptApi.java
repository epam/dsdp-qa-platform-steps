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

package platform.qa.data.common;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import platform.qa.data.entities.ExcerptFormats;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.pojo.common.ExportRequest;
import platform.qa.rest.RestApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * API to work with excerpts
 */
@Log4j2
public class ExcerptApi {
    private Service excerptService;
    private SignatureSteps signatureSteps;

    private static final String EXCERPTS = "excerpts";
    private static final String EXCERPTS_STATUS = "/status";


    public ExcerptApi(Service excerptService, Service digitalSignOps, List<Redis> signatureRedis) {
        this.excerptService = excerptService;
        signatureSteps = new SignatureSteps(excerptService, digitalSignOps, signatureRedis);
    }

    public String createExport(ExportRequest payload) {
        log.info("Виклик API створення витягу");
        String signatureId = signatureSteps.signRequest(payload);

        return new RestApiClient(excerptService, signatureId)
                .postNegative(payload, EXCERPTS)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("excerptIdentifier");
    }

    public String getStatusOfExport(String id) {
        log.info("Виклик API для отримання статусу за витягом {}", id);

        return new RestApiClient(excerptService)
                .get(EXCERPTS + "/" + id + EXCERPTS_STATUS)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("status");
    }

    @SneakyThrows
    public File getExport(String id) {
        log.info("Виклик API для завантаження витягу з id {}", id);
        byte[] bytes = new RestApiClient(excerptService)
                .get(id, EXCERPTS + "/")
                .then()
                .statusCode(200)
                .extract()
                .asByteArray();

        File file = new File("target/report.pdf");
        file.createNewFile();
        OutputStream outStream = new FileOutputStream(file);
        outStream.write(bytes);
        outStream.close();

        return file;
    }

    @SneakyThrows
    public File getExport(String id, ExcerptFormats format) {
        log.info("Виклик API для завантаження витягу з id {} у форматі {}", id, format);

        byte[] bytes = new RestApiClient(excerptService)
                .get(EXCERPTS + "/" + id)
                .then().extract().asByteArray();

        File file = new File("target/report.".concat(format.getFormatName()));
        file.createNewFile();
        OutputStream outStream = new FileOutputStream(file);
        outStream.write(bytes);
        outStream.close();

        return file;
    }

    public ExtractableResponse<Response> tryToCreateWithPayload(ExportRequest payload) {
        log.info("Спроба створення витягу {}", payload);
        String id = signatureSteps.signRequest(payload);

        return new RestApiClient(excerptService, id).postNegative(payload, EXCERPTS)
                .then().extract();
    }
}
