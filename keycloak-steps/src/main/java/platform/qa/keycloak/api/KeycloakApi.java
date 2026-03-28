package platform.qa.keycloak.api;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.internal.mapping.Jackson2Mapper;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.keycloak.pojo.request.SearchEqualsStartWithUserRequest;
import platform.qa.keycloak.pojo.request.SearchUserRequest;
import platform.qa.keycloak.pojo.response.KeycloakError;
import platform.qa.keycloak.pojo.response.KeycloakUser;
import platform.qa.keycloak.pojo.response.KeycloakUserResponse;

import java.util.List;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;

@Log4j2
public class KeycloakApi {

    private final RequestSpecification requestSpec;
    private final static String SEARCH_USER_BY_ATTRIBUTES = "realms/%s/users/%s";

    public KeycloakApi(Service service, User user) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setConfig(
                        config()
                                .logConfig(logConfig()
                                        .enableLoggingOfRequestAndResponseIfValidationFails()
                                        .enablePrettyPrinting(Boolean.TRUE))
                )
                .setContentType("application/json")
                .addHeader("Authorization", "bearer ".concat(user.getToken()))
                .setBaseUri(service.getUrl());

        requestSpec = requestSpecBuilder.build();
    }

    public List<KeycloakUser> searchUsersByAttributes(SearchUserRequest request, String realm) {
        return given()
                .spec(requestSpec)
                .body(request)
                .post(String.format(SEARCH_USER_BY_ATTRIBUTES, realm, "search"))
                .then()
                .extract()
                .as(new TypeToken<List<KeycloakUser>>() {}.getType(), getMapper());
    }

    public KeycloakError searchUserByInvalidAttributes(SearchUserRequest request, String realm) {
        return given()
                .spec(requestSpec)
                .body(request)
                .post(String.format(SEARCH_USER_BY_ATTRIBUTES, realm, "search"))
                .then()
                .extract()
                .as(new TypeToken<KeycloakError>() {}.getType(), getMapper());
    }

    public KeycloakUserResponse searchEqualsStartWithUserRequest(SearchEqualsStartWithUserRequest request,
            String realm) {
        return given()
                .spec(requestSpec)
                .body(request)
                .post(String.format(SEARCH_USER_BY_ATTRIBUTES, realm, "v2/search-by-attributes"))
                .then()
                .extract()
                .as(KeycloakUserResponse.class, getMapper());
    }

    private Jackson2Mapper getMapper() {
        return new Jackson2Mapper((type, s) -> {
            ObjectMapper om = new ObjectMapper().findAndRegisterModules();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return om;
        });
    }
}
