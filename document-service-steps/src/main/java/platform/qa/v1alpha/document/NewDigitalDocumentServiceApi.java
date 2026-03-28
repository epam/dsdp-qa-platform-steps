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

package platform.qa.v1alpha.document;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.nio.file.Files;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.entity.ErrorResponse;
import platform.qa.entity.UploadDocumentResponse;

/**
 * API client for Digital Document Service V1Alpha endpoints Supports document upload and download
 * operations using /v1alpha/documents API
 */
@Log4j2
public class NewDigitalDocumentServiceApi {

  // URI constants
  private static final String UPLOAD_DOCUMENT_URI = "/v1alpha/documents";
  private static final String DOWNLOAD_DOCUMENT_URI = "/v1alpha/documents/{id}";
  private static final String DELETE_DOCUMENT_URI = "/v1alpha/documents/{id}";
  private static final String DOWNLOAD_DOCUMENT_URI_WITHOUT_ID = "/v1alpha/documents";
  private static final String FORM_DATA_CONTENT_TYPE = "multipart/form-data";

  private final RequestSpecification requestSpec;

  public NewDigitalDocumentServiceApi(Service digitalDocService) {
    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    requestSpecBuilder
        .setConfig(
            config()
                .logConfig(
                    logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails()
                        .enablePrettyPrinting(Boolean.TRUE)))
        .setBaseUri(digitalDocService.getUrl())
        .addHeader("X-Access-Token", digitalDocService.getUser().getToken())
        .addHeader("X-XSRF-TOKEN", "Token")
        .addHeader("Cookie", "XSRF-TOKEN=Token");

    requestSpec = requestSpecBuilder.build();
  }

  // ========================================
  // Upload Methods
  // ========================================

  /**
   * Upload document using v1alpha API - Success scenario POST /v1alpha/documents
   *
   * @param file File to upload
   * @return UploadDocumentResponse with id, name, type, checksum, size
   */
  @Step("Upload document via v1alpha API")
  @SneakyThrows
  public UploadDocumentResponse uploadDocument(File file) {
    log.info("Upload document using v1alpha API. file='{}'", file.getName());
    UploadDocumentResponse response =
        given()
            .spec(requestSpec)
            .contentType(FORM_DATA_CONTENT_TYPE)
            .multiPart("file", file, Files.probeContentType(file.toPath()))
            .when()
            .post(UPLOAD_DOCUMENT_URI)
            .then()
            .statusCode(SC_OK)
            .extract()
            .as(UploadDocumentResponse.class);

    log.info("Document uploaded successfully via v1alpha API, id: {}", response.getId());
    return response;
  }

  /**
   * Upload document using v1alpha API with custom filename - Success scenario POST
   * /v1alpha/documents
   *
   * @param file File to upload
   * @param customFilename Custom filename to use for the uploaded file
   * @return UploadDocumentResponse with id, name, type, checksum, size
   */
  @Step("Upload document with custom filename '{customFilename}' via v1alpha API")
  @SneakyThrows
  public UploadDocumentResponse uploadDocument(File file, String customFilename) {
    log.info(
        "Upload document using v1alpha API with custom filename. file='{}', customFilename='{}'",
        file.getName(),
        customFilename);
    UploadDocumentResponse response =
        given()
            .spec(requestSpec)
            .contentType(FORM_DATA_CONTENT_TYPE)
            .multiPart("file", file, Files.probeContentType(file.toPath()))
            .multiPart("filename", customFilename)
            .when()
            .post(UPLOAD_DOCUMENT_URI)
            .then()
            .statusCode(SC_OK)
            .extract()
            .as(UploadDocumentResponse.class);

    log.info(
        "Document uploaded successfully via v1alpha API with custom filename, id: {}",
        response.getId());
    return response;
  }

  /**
   * Upload document using v1alpha API - Error scenario POST /v1alpha/documents
   *
   * @param file File to upload
   * @param statusCode Expected HTTP status code
   * @return ErrorResponse with error details
   */
  @Step("Upload document via v1alpha API expecting status code {statusCode}")
  @SneakyThrows
  public ErrorResponse uploadDocument(File file, Integer statusCode) {
    log.info(
        "Upload document using v1alpha API expecting error. file='{}', expectedStatusCode={}",
        file.getName(),
        statusCode);
    return given()
        .spec(requestSpec)
        .contentType(FORM_DATA_CONTENT_TYPE)
        .multiPart("file", file, Files.probeContentType(file.toPath()))
        .when()
        .post(UPLOAD_DOCUMENT_URI)
        .then()
        .statusCode(statusCode)
        .extract()
        .as(ErrorResponse.class);
  }

  /**
   * Upload document using v1alpha API with specific content type - Error scenario POST
   * /v1alpha/documents Used for testing unsupported media types
   *
   * @param file File to upload
   * @param contentType Content type to use in the request
   * @param statusCode Expected HTTP status code
   * @return ErrorResponse with error details
   */
  @Step(
      "Upload document with content type '{contentType}' via v1alpha API expecting status code {statusCode}")
  public ErrorResponse uploadDocument(File file, String contentType, Integer statusCode) {
    log.info(
        "Upload document using v1alpha API with custom content type expecting error. file='{}', contentType='{}',"
            + " expectedStatusCode={}",
        file.getName(),
        contentType,
        statusCode);
    return given()
        .spec(requestSpec)
        .contentType(FORM_DATA_CONTENT_TYPE)
        .multiPart("file", file, contentType)
        .when()
        .post(UPLOAD_DOCUMENT_URI)
        .then()
        .statusCode(statusCode)
        .extract()
        .as(ErrorResponse.class);
  }

  // ========================================
  // Download Methods
  // ========================================

  /**
   * Download document using v1alpha API as byte array - Success scenario GET
   * /v1alpha/documents/{id}
   *
   * @param documentId Document ID to download
   * @return Byte array of the document content
   */
  @Step("Download document with ID '{documentId}' as byte array via v1alpha API")
  public byte[] downloadDocumentAsByteArray(String documentId) {
    log.info("Download document using v1alpha API as byte array. documentId='{}'", documentId);
    return given()
        .spec(requestSpec)
        .pathParam("id", documentId)
        .when()
        .get(DOWNLOAD_DOCUMENT_URI)
        .then()
        .statusCode(SC_OK)
        .extract()
        .asByteArray();
  }

  /**
   * Download document using v1alpha API - Error scenario GET /v1alpha/documents/{id}
   *
   * @param documentId Document ID to download
   * @param statusCode Expected HTTP status code
   * @return ErrorResponse with error details
   */
  @Step(
      "Download document with ID '{documentId}' via v1alpha API expecting status code {statusCode}")
  public ErrorResponse downloadDocument(String documentId, Integer statusCode) {
    log.info(
        "Download document using v1alpha API expecting error. documentId='{}', expectedStatusCode={}",
        documentId,
        statusCode);
    return given()
        .spec(requestSpec)
        .pathParam("id", documentId)
        .when()
        .get(DOWNLOAD_DOCUMENT_URI)
        .then()
        .statusCode(statusCode)
        .extract()
        .as(ErrorResponse.class);
  }

  @Step(
      "Download document with ID '{documentId}' via v1alpha API expecting status code {statusCode}")
  public ErrorResponse downloadDocumentWithOutPathParam(Integer statusCode) {
    log.info(
        "Download document using v1alpha API expecting error. expectedStatusCode={}", statusCode);
    return given()
        .spec(requestSpec)
        .when()
        .get(DOWNLOAD_DOCUMENT_URI_WITHOUT_ID)
        .then()
        .statusCode(statusCode)
        .extract()
        .as(ErrorResponse.class);
  }

  // ========================================
  // Delete Methods
  // ========================================

  /**
   * Delete document using v1alpha API - Success scenario DELETE /v1alpha/documents/{id}
   *
   * @param documentId Document ID to delete
   */
  @Step("Delete document with ID '{documentId}' via v1alpha API")
  public void deleteDocument(String documentId) {
    log.info("Delete document using v1alpha API. documentId='{}'", documentId);

    given()
        .spec(requestSpec)
        .pathParam("id", documentId)
        .when()
        .delete(DELETE_DOCUMENT_URI)
        .then()
        .statusCode(SC_NO_CONTENT);

    log.info("Document deleted successfully via v1alpha API. documentId='{}'", documentId);
  }

  /**
   * Delete document using v1alpha API - Error scenario DELETE /v1alpha/documents/{id}
   *
   * @param documentId Document ID to delete
   * @param statusCode Expected HTTP status code
   * @return ErrorResponse with error details
   */
  @Step(
      "Delete document with ID '{documentId}' via v1alpha API expecting status code {statusCode}")
  public ErrorResponse deleteDocument(String documentId, Integer statusCode) {
    log.info(
        "Delete document using v1alpha API expecting error. documentId='{}', expectedStatusCode={}",
        documentId,
        statusCode);
    return given()
        .spec(requestSpec)
        .pathParam("id", documentId)
        .when()
        .delete(DELETE_DOCUMENT_URI)
        .then()
        .statusCode(statusCode)
        .extract()
        .as(ErrorResponse.class);
  }
}
