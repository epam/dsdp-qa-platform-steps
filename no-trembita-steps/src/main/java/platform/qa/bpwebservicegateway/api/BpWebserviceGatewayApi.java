package platform.qa.bpwebservicegateway.api;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.bpwebservicegateway.pojo.request.StartBpData;
import platform.qa.bpwebservicegateway.pojo.request.StartBpParams;
import platform.qa.entities.Service;
import platform.qa.entities.User;

@Log4j2
public class BpWebserviceGatewayApi {

    private static final String START_BP_PATH = "start-bp";
    private final RequestSpecification requestSpec;

    public BpWebserviceGatewayApi(Service serviceBpGateway, User userAccessToProcess) {
        //TODO remove replacement
        String url = serviceBpGateway.getUrl().replace(":443/ws", "");
        requestSpec = new RequestSpecBuilder().setConfig(
                        config()
                                .logConfig(logConfig()
                                        .enableLoggingOfRequestAndResponseIfValidationFails()
                                        .enablePrettyPrinting(Boolean.TRUE)))
                .setContentType(ContentType.JSON)
                .setBaseUri(url + "/")
                .addHeader("X-Access-Token", userAccessToProcess.getToken()).build();
    }

    public Response startBusinessProcess(StartBpData startVariables) {
        return given()
                .spec(requestSpec)
                .basePath(START_BP_PATH)
                .body(startVariables)
                .post();
    }

    public Response startBusinessProcess(String processDefinition, StartBpParams startVariables) {
        return given()
                .spec(requestSpec)
                .basePath(START_BP_PATH.concat("/").concat(processDefinition))
                .body(startVariables)
                .post();
    }
}
