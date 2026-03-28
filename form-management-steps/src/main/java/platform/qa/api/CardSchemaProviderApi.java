package platform.qa.api;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpStatus;
import platform.qa.entities.Service;

@Log4j2
public class CardSchemaProviderApi {

  public static final String X_ACCESS_TOKEN = "X-Access-Token";

  private static final String GET_CARD_BY_KEY_ENDPOINT = "/cards/{key}";
  private static final String GET_CARDS_META_ENDPOINT = "/cards/meta";

  private final RequestSpecification requestSpec;
  private final String baseUrl;

  public CardSchemaProviderApi(Service cardSchemaService) {
    this.baseUrl = cardSchemaService.getUrl() + "/api";

    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    requestSpecBuilder
        .setConfig(
            config()
                .logConfig(
                    logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails()
                        .enablePrettyPrinting(true)))
        .setBaseUri(this.baseUrl)
        .addHeader("Content-Type", "application/json")
        .addHeader(X_ACCESS_TOKEN, cardSchemaService.getUser().getToken())
        .addHeader("X-XSRF-TOKEN", "Token")
        .addHeader("Cookie", "XSRF-TOKEN=Token");

    requestSpec = requestSpecBuilder.build();
  }

  @Step("Get card by key: {key}")
  public Map<String, Object> getCardByKey(String key) {
    log.info("Get card by key: {} as Map", key);

    return given()
        .spec(requestSpec)
        .get(GET_CARD_BY_KEY_ENDPOINT, key)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .extract()
        .as(new TypeRef<>() {});
  }

  @Step("Send GET request for card by key: {key}")
  public Response sendGetCardByKey(String key) {
    log.info("Get card by key: {} as Response", key);

    return given().spec(requestSpec).get(GET_CARD_BY_KEY_ENDPOINT, key);
  }

  @Step("Get short metadata of all cards")
  public List<Map<String, String>> getCardsMeta() {
    log.info("Get cards short metadata list");

    return given()
        .spec(requestSpec)
        .get(GET_CARDS_META_ENDPOINT)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .contentType(ContentType.JSON)
        .extract()
        .as(List.class);
  }

  @Step("Send GET request for cards meta")
  public Response sendGetCardsMeta() {
    log.info("Get cards meta as Response");

    return given().spec(requestSpec).get(GET_CARDS_META_ENDPOINT);
  }

  @Step("Send GET request for card: {key} without authentication")
  public Response sendGetCardWithoutAuth(String key) {
    log.info("Get card: {} without authentication", key);

    return given().baseUri(baseUrl).get(GET_CARD_BY_KEY_ENDPOINT, key);
  }

  @Step("Send GET request for card: {key} with custom token")
  public Response sendGetCardWithCustomToken(String key, String token) {
    log.info("Get card: {} with custom token", key);

    return given()
        .baseUri(baseUrl)
        .header(X_ACCESS_TOKEN, token)
        .get(GET_CARD_BY_KEY_ENDPOINT, key);
  }

  @Step("Send GET request for cards meta without authentication")
  public Response sendGetCardsMetaWithoutAuth() {
    log.info("Get cards meta without authentication");

    return given().baseUri(baseUrl).get(GET_CARDS_META_ENDPOINT);
  }

  @Step("Send GET request for cards meta with custom token")
  public Response sendGetCardsMetaWithCustomToken(String token) {
    log.info("Get cards meta with custom token");

    return given().baseUri(baseUrl).header(X_ACCESS_TOKEN, token).get(GET_CARDS_META_ENDPOINT);
  }
}
