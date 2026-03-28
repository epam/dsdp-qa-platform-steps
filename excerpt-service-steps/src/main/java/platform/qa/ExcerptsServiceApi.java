package platform.qa;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static org.apache.http.HttpStatus.SC_OK;
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
import platform.qa.entities.Service;
import platform.qa.pojo.response.ExcerptsResponse;

@Log4j2
public class ExcerptsServiceApi {

  private final RequestSpecification requestSpec;

  public ExcerptsServiceApi(Service service) {
    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    requestSpecBuilder
        .setConfig(
            config()
                .logConfig(
                    logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails()
                        .enablePrettyPrinting(Boolean.TRUE)))
        .setBaseUri(service.getUrl())
        .addHeader(X_ACCESS_TOKEN_HEADER, service.getUser().getToken())
        .addHeader(XSRF_HEADER_NAME, XSRF_HEADER_VALUE)
        .addHeader(COOKIE_HEADER_NAME, COOKIE_HEADER_VALUE);

    requestSpec = requestSpecBuilder.build();
  }

  @Step("Retrieve excerpt by id")
  public ExcerptsResponse getExcerptById(String id) {
    log.info("Getting Excerpt with id: {}", id);
    return given()
        .spec(requestSpec)
        .when()
        .get(ID, id)
        .then()
        .statusCode(SC_OK)
        .extract()
        .as(ExcerptsResponse.class);
  }
}
