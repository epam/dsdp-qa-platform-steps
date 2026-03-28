package platform.qa.api;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import platform.qa.entities.Service;

public class FormSubmissionValidationApi {

    private final RequestSpecification requestSpec;

    public FormSubmissionValidationApi(Service service) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setConfig(
                        config()
                                .logConfig(logConfig()
                                        .enableLoggingOfRequestAndResponseIfValidationFails()
                                        .enablePrettyPrinting(Boolean.TRUE)))
                .setBaseUri(service.getUrl() + "api")
                .addHeader("Content-Type", "application/json");
        requestSpec = requestSpecBuilder.build();
    }

    public ValidatableResponse validateForm(String formKey, String accessToken, Object body) {
        var request = given()
                .spec(requestSpec)
                .header("X-Request-Id", java.util.UUID.randomUUID().toString())
                .header("X-B3-TraceId", java.util.UUID.randomUUID().toString().replace("-", ""))
                .header("X-B3-SpanId", java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        if (accessToken != null) {
            request = request.header("X-Access-Token", accessToken);
        }
        return request.body(body)
                .when()
                .post("/form-submissions/" + formKey + "/validate")
                .then();
    }

    public ValidatableResponse validateFormMissingToken(String formKey, Object body) {
        return given()
                .spec(requestSpec)
                .header("X-Request-Id", java.util.UUID.randomUUID().toString())
                .header("X-B3-TraceId", "X-B3-TraceId")
                .body(body)
                .when()
                .post("/form-submissions/" + formKey + "/validate")
                .then();
    }

    public ValidatableResponse validateFormRaw(String formKey, String accessToken, String rawBody) {
        var request = given()
                .spec(requestSpec)
                .header("X-B3-TraceId", "X-B3-TraceId");
        if (accessToken != null) {
            request = request.header("X-Access-Token", accessToken);
        }
        return request.body(rawBody)
                .when()
                .post("/form-submissions/" + formKey + "/validate")
                .then();
    }

    public ValidatableResponse validateField(String formKey, String fieldKey, String accessToken, Object body) {
        var request = given()
                .spec(requestSpec)
                .header("X-B3-TraceId", "X-B3-TraceId");
        if (accessToken != null) {
            request = request.header("X-Access-Token", accessToken);
        }
        return request.body(body)
                .when()
                .post("/form-submissions/" + formKey + "/fields/" + fieldKey + "/validate")
                .then();
    }

    public ValidatableResponse validateFieldRaw(String formKey, String fieldKey, String accessToken, String rawBody) {
        var request = given()
                .spec(requestSpec)
                .header("X-B3-TraceId", "X-B3-TraceId");
        if (accessToken != null) {
            request = request.header("X-Access-Token", accessToken);
        }
        return request.body(rawBody)
                .when()
                .post("/form-submissions/" + formKey + "/fields/" + fieldKey + "/validate")
                .then();
    }

    public ValidatableResponse checkFields(String formKey, String accessToken, Object body) {
        var request = given()
                .spec(requestSpec)
                .header("X-B3-TraceId", "X-B3-TraceId");
        if (accessToken != null) {
            request = request.header("X-Access-Token", accessToken);
        }
        return request.body(body)
                .when()
                .post("/form-submissions/" + formKey + "/fields/check")
                .then();
    }

    public ValidatableResponse checkFieldsRaw(String formKey, String accessToken, String rawBody) {
        var request = given()
                .spec(requestSpec)
                .header("X-B3-TraceId", "X-B3-TraceId");
        if (accessToken != null) {
            request = request.header("X-Access-Token", accessToken);
        }
        return request.body(rawBody)
                .when()
                .post("/form-submissions/" + formKey + "/fields/check")
                .then();
    }
}