package platform.qa;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static platform.qa.constants.Constants.CONTENT_TYPE_HEADER;
import static platform.qa.constants.Constants.CONTENT_TYPE_VALUE;
import static platform.qa.constants.Constants.COOKIE_HEADER_NAME;
import static platform.qa.constants.Constants.COOKIE_HEADER_VALUE;
import static platform.qa.constants.Constants.ID;
import static platform.qa.constants.Constants.XSRF_HEADER_NAME;
import static platform.qa.constants.Constants.XSRF_HEADER_VALUE;
import static platform.qa.constants.Constants.X_ACCESS_TOKEN_HEADER;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpStatus;
import platform.qa.entities.Service;
import platform.qa.pojo.request.ExcerptsTemplateInternalRequest;
import platform.qa.pojo.response.ExcerptsTemplateInternalResponse;

@Log4j2
public class ExcerptsTemplateInternalApi {

  private final RequestSpecification requestSpec;

  public ExcerptsTemplateInternalApi(Service service) {
    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    requestSpecBuilder
        .setConfig(
            config()
                .logConfig(
                    logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails()
                        .enablePrettyPrinting(Boolean.TRUE)))
        .setBaseUri(service.getUrl())
        .setBasePath("/internal-api/excerpts")
        .addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
        .addHeader(X_ACCESS_TOKEN_HEADER, service.getUser().getToken())
        .addHeader(XSRF_HEADER_NAME, XSRF_HEADER_VALUE)
        .addHeader(COOKIE_HEADER_NAME, COOKIE_HEADER_VALUE);

    requestSpec = requestSpecBuilder.build();
  }

  @Step("Create excerpt template via internal API")
  public ExcerptsTemplateInternalResponse createExcerptTemplate(
      ExcerptsTemplateInternalRequest request) {
    log.info("Creating Excerpt template with payload: {}", request);
    return given()
        .spec(requestSpec)
        .body(request)
        .when()
        .post()
        .then()
        .statusCode(SC_CREATED)
        .extract()
        .as(ExcerptsTemplateInternalResponse.class);
  }

  @Step("Retrieve excerpt template by id via internal API")
  public ExcerptsTemplateInternalResponse getExcerptTemplateById(String id) {
    log.info("Getting Excerpt template with id: {}", id);
    return given()
        .spec(requestSpec)
        .when()
        .get(ID, id)
        .then()
        .statusCode(SC_OK)
        .extract()
        .as(ExcerptsTemplateInternalResponse.class);
  }

  @Step("Delete excerpt template by id via internal API")
  public void deleteExcerptTemplateById(String id) {
    log.info("Deleting Excerpt template with id: {}", id);
    given().spec(requestSpec).delete(ID, id).then().statusCode(HttpStatus.SC_NO_CONTENT);
  }
}
