package platform.qa.usermanagement.v1alpha.services;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.usermanagement.pojo.response.CreateApplicationResponse;
import platform.qa.usermanagement.pojo.response.ErrorResponse;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static platform.qa.usermanagement.constants.Constants.*;

/**
* API client for /v1alpha/services endpoints
*/
@Log4j2
public class UserProcessMngmServiceApi {

// URI constants
  private static final String CREATE_APPLICATION_URI = "user-process-management/api/v1alpha/services/{id}" +
        "/applications";
  private static final String GET_APPLICATION_URI = "user-process-management/api/v1alpha/applications" +
        "/{applicationId}";

  private final static String CREATE_APPLICATION_LOG = "Creating application for service: {}";
  private final static String APPLICATION_ID = "applicationId";
  private final RequestSpecification requestSpec;

  public UserProcessMngmServiceApi(Service service) {
    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    requestSpecBuilder.setConfig(
                    config()
                            .logConfig(logConfig()
                                    .enableLoggingOfRequestAndResponseIfValidationFails()
                                    .enablePrettyPrinting(Boolean.TRUE)))
            .setBaseUri(service.getUrl())
            .setContentType(ContentType.JSON)
            .addHeader(X_ACCESS_TOKEN_HEADER, service.getUser().getToken())
            .addHeader(XSRF_HEADER_NAME, XSRF_HEADER_VALUE)
            .addHeader(COOKIE_HEADER_NAME, COOKIE_HEADER_VALUE);

    requestSpec = requestSpecBuilder.build();
}

/**
 * POST /v1alpha/services/{id}/applications
 * Create application for digital service
 *
 * @param serviceId   the service ID
 * @param formPayload the form data payload
 * @return CreateApplicationResponse with 201 status
 */
  @Step("Create application for service '{serviceId}'")
  public CreateApplicationResponse createApplication(String serviceId, Object formPayload) {
    log.info(CREATE_APPLICATION_LOG, serviceId);
    return given()
            .spec(requestSpec)
            .pathParam("id", serviceId)
            .body(formPayload)
            .when()
            .post(CREATE_APPLICATION_URI)
            .then()
            .statusCode(SC_CREATED)
            .extract()
            .as(CreateApplicationResponse.class);
}

/**
 * POST /v1alpha/services/{id}/applications
 * Create application for digital service
 *
 * @param serviceId the service ID
 * @return CreateApplicationResponse with 201 status
 */
  @Step("Create application for service '{serviceId}'")
  public CreateApplicationResponse createApplication(String serviceId) {
    log.info(CREATE_APPLICATION_LOG, serviceId);
    return given()
            .spec(requestSpec)
            .pathParam("id", serviceId)
            .when()
            .post(CREATE_APPLICATION_URI)
            .then()
            .statusCode(SC_CREATED)
            .extract()
            .as(CreateApplicationResponse.class);
}

/**
 * POST /v1alpha/services/{id}/applications with custom status code
 *
 * @param serviceId   the service ID
 * @param formPayload the form data payload
 * @param statusCode  expected status code
 * @return ErrorResponse
 */
  @Step("Create application for service '{serviceId}' expecting status code {statusCode}")
  public ErrorResponse createApplication(String serviceId, Object formPayload, int statusCode) {
    log.info(CREATE_APPLICATION_LOG + " with expected status: {}", serviceId, statusCode);
    return given()
            .spec(requestSpec)
            .pathParam("id", serviceId)
            .body(formPayload)
            .when()
            .post(CREATE_APPLICATION_URI)
            .then()
            .statusCode(statusCode)
            .extract()
            .as(ErrorResponse.class);
}

/**
 * GET /v1alpha/applications/{applicationId}
 * Get application details by ID
 *
 * @param applicationId the application ID
 * @return CreateApplicationResponse with 200 status (reusing the same response structure)
 */
  @Step("Get application with ID '{applicationId}'")
  public CreateApplicationResponse getApplication(String applicationId) {
    log.info("Getting application details: {}", applicationId);
    return given()
            .spec(requestSpec)
            .pathParam(APPLICATION_ID, applicationId)
            .when()
            .get(GET_APPLICATION_URI)
            .then()
            .statusCode(SC_OK)
            .extract()
            .as(CreateApplicationResponse.class);
}

/**
 * GET /v1alpha/applications/{applicationId} with custom status code
 *
 * @param applicationId the application ID
 * @param statusCode    expected status code
 * @return ErrorResponse
 */
  @Step("Get application with ID '{applicationId}' expecting status code {statusCode}")
  public ErrorResponse getApplication(String applicationId, int statusCode) {
    log.info("Getting application details: {} with expected status: {}", applicationId, statusCode);
    return given()
            .spec(requestSpec)
            .pathParam(APPLICATION_ID, applicationId)
            .when()
            .get(GET_APPLICATION_URI)
            .then()
            .statusCode(statusCode)
            .extract()
            .as(ErrorResponse.class);
}
}

