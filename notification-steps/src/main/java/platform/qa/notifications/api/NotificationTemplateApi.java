package platform.qa.notifications.api;

import static io.restassured.RestAssured.given;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.entities.User;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpStatus;

/**
 * API client for managing notification templates
 * Provides methods to create, retrieve, and delete notification templates for different communication channels
 */
@Log4j2
public class NotificationTemplateApi {

    private final RequestSpecification requestSpec;
    private static final String TEMPLATES_ENDPOINT = "/api/notifications/templates";
    private static final String TEMPLATE_BY_CHANNEL_AND_NAME_ENDPOINT = "/api/notifications/templates/{channel}:{name}";
    private static final String TEMPLATE_BY_ID_ENDPOINT = "/api/notifications/templates/{id}";

    public NotificationTemplateApi(Service service, User user) {
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(service.getUrl())
                .addHeader("X-Access-Token", user.getToken()).build();
    }

    /**
     * Creates or updates a notification template for a specific communication channel
     * 
     * @param channel the communication channel (e.g., email, sms, push)
     * @param name the template name
     * @param templatePayload the template content and configuration
     * @return Response object for further validation
     */
    public Response putNotificationTemplate(String channel, String name, Object templatePayload) {
        log.info("PUT notification template for channel: {} and name: {}", channel, name);
        return given()
                .spec(requestSpec)
                .body(templatePayload)
                .put(TEMPLATE_BY_CHANNEL_AND_NAME_ENDPOINT, channel, name);
    }

    /**
     * Creates or updates a notification template with status validation
     * 
     * @param channel the communication channel
     * @param name the template name
     * @param templatePayload the template content and configuration
     */
    public void createOrUpdateNotificationTemplate(String channel, String name, Object templatePayload) {
        log.info("Create/Update notification template for channel: {} and name: {} with status check", channel, name);
        given()
                .spec(requestSpec)
                .body(templatePayload)
                .put(TEMPLATE_BY_CHANNEL_AND_NAME_ENDPOINT, channel, name)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    /**
     * Retrieves all notification templates
     * 
     * @return Response object containing all templates
     */
    public Response getAllNotificationTemplates() {
        log.info("GET all notification templates");
        return given()
                .spec(requestSpec)
                .get(TEMPLATES_ENDPOINT);
    }

    /**
     * Retrieves all notification templates as a list
     * 
     * @return List of notification template objects
     */
    public List<Map<String, Object>> getNotificationTemplatesList() {
        log.info("GET all notification templates as list");
        return getAllNotificationTemplates()
                .as(new TypeReference<List<Map<String, Object>>>() {
                }.getType());
    }

    /**
     * Retrieves all notification templates with status validation
     * 
     * @return List of notification template objects
     */
    public List<Map<String, Object>> getAllNotificationTemplatesWithValidation() {
        log.info("GET all notification templates with status validation");
        return given()
                .spec(requestSpec)
                .get(TEMPLATES_ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(new TypeReference<List<Map<String, Object>>>() {
                }.getType());
    }

    /**
     * Deletes a notification template by ID
     * 
     * @param templateId the ID of the template to delete
     * @return Response object for further validation
     */
    public Response deleteNotificationTemplate(String templateId) {
        log.info("DELETE notification template with ID: {}", templateId);
        return given()
                .spec(requestSpec)
                .delete(TEMPLATE_BY_ID_ENDPOINT, templateId);
    }

    /**
     * Deletes a notification template by ID with status validation
     * 
     * @param templateId the ID of the template to delete
     */
    public void deleteNotificationTemplateWithValidation(String templateId) {
        log.info("DELETE notification template with ID: {} with status check", templateId);
        given()
                .spec(requestSpec)
                .delete(TEMPLATE_BY_ID_ENDPOINT, templateId)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }
}
