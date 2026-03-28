package platform.qa.usermanagement.v1alpha.step;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static org.apache.http.HttpStatus.SC_OK;
import static platform.qa.usermanagement.constants.Constants.COOKIE_HEADER_NAME;
import static platform.qa.usermanagement.constants.Constants.COOKIE_HEADER_VALUE;
import static platform.qa.usermanagement.constants.Constants.XSRF_HEADER_NAME;
import static platform.qa.usermanagement.constants.Constants.XSRF_HEADER_VALUE;
import static platform.qa.usermanagement.constants.Constants.X_ACCESS_TOKEN_HEADER;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.usermanagement.pojo.response.StepResponse;
import platform.qa.usermanagement.pojo.response.SubmitStepResponse;
import platform.qa.usermanagement.pojo.response.ErrorResponse;

/**
* API client for /v1alpha/step endpoints
*/
@Log4j2
public class UserProcessMngmStepApi {

// URI constants
  private static final String SUBMIT_STEP_URI = "user-process-management/api/v1alpha/applications/" +
        "{applicationId}/steps/{id}/submit";
  private static final String GET_STEP_URI = "user-process-management/api/v1alpha/applications/" +
        "{applicationId}/steps/{id}";

  private final static String APPLICATION_ID = "applicationId";
  private final RequestSpecification requestSpec;

  public UserProcessMngmStepApi(Service service) {
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
 * POST /v1alpha/applications/{applicationId}/steps/{id}/submit
 * Submit a step
 *
 * @param applicationId the application ID
 * @param stepId the step ID
 * @param formPayload the form data payload
 * @return SubmitStepResponse with 200 status
 */
  @Step("Submit step '{stepId}' for application '{applicationId}'")
  public SubmitStepResponse submitStep(String applicationId, String stepId, Object formPayload) {
    log.info("Submitting step: {} for application: {}", stepId, applicationId);
    return given()
            .spec(requestSpec)
            .pathParam(APPLICATION_ID, applicationId)
            .pathParam("id", stepId)
            .body(formPayload)
            .when()
            .post(SUBMIT_STEP_URI)
            .then()
            .statusCode(SC_OK)
            .extract()
            .as(SubmitStepResponse.class);
}

/**
 * POST /v1alpha/applications/{applicationId}/steps/{id}/submit with custom status code
 *
 * @param applicationId the application ID
 * @param stepId the step ID
 * @param formPayload the form data payload
 * @param statusCode expected status code
 * @return ErrorResponse
 */
  @Step("Submit step '{stepId}' for application '{applicationId}' expecting status code {statusCode}")
  public ErrorResponse submitStep(String applicationId, String stepId, Object formPayload, int statusCode) {
    log.info("Submitting step: {} for application: {} with expected status: {}", stepId, applicationId,
            statusCode);
    return given()
            .spec(requestSpec)
            .pathParam(APPLICATION_ID, applicationId)
            .pathParam("id", stepId)
            .body(formPayload)
            .when()
            .post(SUBMIT_STEP_URI)
            .then()
            .statusCode(statusCode)
            .extract()
            .as(ErrorResponse.class);
}

/**
 * GET /v1alpha/applications/{applicationId}/steps/{id}
 * Get step details
 *
 * @param applicationId the application ID
 * @param stepId the step ID
 * @return StepResponse
 */
  @Step("Get step '{stepId}' for application '{applicationId}'")
  public StepResponse getStep(String applicationId, String stepId) {
    log.info("Getting step details: {} for application: {}", stepId, applicationId);
    return given()
            .spec(requestSpec)
            .pathParam(APPLICATION_ID, applicationId)
            .pathParam("id", stepId)
            .when()
            .get(GET_STEP_URI)
            .then()
            .statusCode(SC_OK)
            .extract()
            .as(StepResponse.class);
}

/**
 * GET /v1alpha/applications/{applicationId}/steps/{id} with custom status code
 *
 * @param applicationId the application ID
 * @param stepId the step ID
 * @param statusCode expected status code
 * @return ErrorResponse
 */
  @Step("Get step '{stepId}' for application '{applicationId}' expecting status code {statusCode}")
  public ErrorResponse getStep(String applicationId, String stepId, int statusCode) {
    log.info("Getting step details: {} for application: {} with expected status: {}", stepId,
            applicationId, statusCode);
    return given()
            .spec(requestSpec)
            .pathParam(APPLICATION_ID, applicationId)
            .pathParam("id", stepId)
            .when()
            .get(GET_STEP_URI)
            .then()
            .statusCode(statusCode)
            .extract()
            .as(ErrorResponse.class);
}
}