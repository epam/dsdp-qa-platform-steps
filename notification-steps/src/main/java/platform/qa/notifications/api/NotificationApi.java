package platform.qa.notifications.api;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.notifications.pojo.response.InboxNotification;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

@Log4j2
public class NotificationApi {

    private final RequestSpecification requestSpec;
    private static final String NOTIFICATIONS_INBOX_ENDPOINT = "/api/notifications/inbox";
    private static final String NOTIFICATION_ACK_ENDPOINT = NOTIFICATIONS_INBOX_ENDPOINT + "/{id}/ack";

    public NotificationApi(Service service, User user) {
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(service.getUrl())
                .addHeader("X-Access-Token", user.getToken()).build();
    }

    /**
     * GET /api/notifications/inbox - Retrieve notifications with basic parameters
     * 
     * @param offset Starting position for pagination (default: 0)
     * @param limit Maximum number of records to return (default: 10)
     * @return Response containing list of notifications
     */
    public Response getNotifications(int offset, int limit) {
        log.info("Getting notifications with offset: {} and limit: {}", offset, limit);
        return given()
                .spec(requestSpec)
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .get(NOTIFICATIONS_INBOX_ENDPOINT);
    }

    /**
     * GET /api/notifications/inbox - Retrieve notifications with sorting
     * 
     * @param offset Starting position for pagination
     * @param limit Maximum number of records to return
     * @param sort Field and order for sorting (e.g., "desc(endTime)", "asc(createdAt)")
     * @return Response containing list of notifications
     */
    public Response getNotifications(int offset, int limit, String sort) {
        log.info("Getting notifications with offset: {}, limit: {}, sort: {}", offset, limit, sort);
        return given()
                .spec(requestSpec)
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .queryParam("sort", sort)
                .get(NOTIFICATIONS_INBOX_ENDPOINT);
    }

    /**
     * GET /api/notifications/inbox - Retrieve notifications with request object
     * 
     * @param requestObject Complex request object containing pagination and sorting parameters
     * @return Response containing list of notifications
     */
    public Response getNotifications(Map<String, Object> requestObject) {
        log.info("Getting notifications with request object: {}", requestObject);
        return given()
                .spec(requestSpec)
                .queryParam("request", requestObject)
                .get(NOTIFICATIONS_INBOX_ENDPOINT);
    }

    /**
     * GET /api/notifications/inbox - Retrieve notifications with all parameters
     * 
     * @param offset Starting position for pagination
     * @param limit Maximum number of records to return
     * @param sort Field and order for sorting
     * @param requestObject Complex request object
     * @return Response containing list of notifications
     */
    public Response getNotifications(int offset, int limit, String sort, Map<String, Object> requestObject) {
        log.info("Getting notifications with offset: {}, limit: {}, sort: {}, request: {}", 
                offset, limit, sort, requestObject);
        return given()
                .spec(requestSpec)
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .queryParam("sort", sort)
                .queryParam("request", requestObject)
                .get(NOTIFICATIONS_INBOX_ENDPOINT);
    }

    /**
     * POST /api/notifications/inbox/{id}/ack - Acknowledge notification
     * 
     * This endpoint is used for confirming notification about the status or result 
     * of the business process, receiving official messages.
     * 
     * Authorization: Requires valid user authentication via X-Access-Token header.
     * Only the recipient of the notification can update its state.
     * 
     * @param notificationId UUID of the notification to acknowledge
     * @return Response from the acknowledgment request
     */
    public Response changeState(String notificationId) {
        log.info("Acknowledging notification with ID: {}", notificationId);
        return given()
                .spec(requestSpec)
                .pathParam("id", notificationId)
                .post(NOTIFICATION_ACK_ENDPOINT);
    }

    /**
     * POST /api/notifications/inbox/{id}/ack - Acknowledge notification with validation
     * 
     * @param notificationId UUID of the notification to acknowledge
     */
    public void acknowledgeNotification(String notificationId) {
        changeState(notificationId)
                .then()
                .statusCode(SC_NO_CONTENT);
        log.info("Notification with ID: {} acknowledged successfully.", notificationId);
    }

    /**
     * GET /api/notifications/inbox - Retrieve notifications as typed list
     * 
     * @param offset Starting position for pagination
     * @param limit Maximum number of records to return
     * @return List of InboxNotification objects
     */
    public List<InboxNotification> getNotificationList(int offset, int limit) {
        return getNotifications(offset, limit)
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(new TypeReference<List<InboxNotification>>() {
                }.getType());
    }

    /**
     * GET /api/notifications/inbox - Retrieve notifications as typed list with sorting
     * 
     * @param offset Starting position for pagination
     * @param limit Maximum number of records to return
     * @param sort Field and order for sorting
     * @return List of InboxNotification objects
     */
    public List<InboxNotification> getNotificationList(int offset, int limit, String sort) {
        return getNotifications(offset, limit, sort)
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(new TypeReference<List<InboxNotification>>() {
                }.getType());
    }

    /**
     * GET /api/notifications/inbox - Retrieve notifications with validation
     * 
     * @param offset Starting position for pagination
     * @param limit Maximum number of records to return
     * @return List of InboxNotification objects with status validation
     */
    public List<InboxNotification> getNotificationsWithValidation(int offset, int limit) {
        log.info("Retrieving notifications with status validation - offset: {}, limit: {}", offset, limit);
        return getNotifications(offset, limit)
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(new TypeReference<List<InboxNotification>>() {
                }.getType());
    }
}
