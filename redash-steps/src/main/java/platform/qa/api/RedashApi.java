/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import platform.qa.clients.RedashClient;
import platform.qa.entities.IEntity;
import platform.qa.entities.Service;
import platform.qa.pojo.redash.CreateDashboardRequest;
import platform.qa.pojo.redash.CreateRoleRequest;
import platform.qa.pojo.redash.CreateTextboxRequest;
import platform.qa.pojo.redash.PublishDashboardRequest;
import platform.qa.pojo.redash.RedashUser;
import platform.qa.pojo.redash.RedashUserPassword;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
public class RedashApi {
    private RedashClient redashClient;
    private Service redash;

    public RedashApi(Service service) {
        redashClient = new RedashClient(service);
        redash = service;
    }

    public RedashApi(Service service, RedashUser user) {
        redashClient = new RedashClient(service, user);
        redash = service;
    }

    public static final String DASHBOARD_URL = "/dashboards";
    public static final String DATA_SOURCE_URL = "/data_sources";
    public static final String GROUPS_URL = "/groups";
    private static final String QUERIES_URL = "/queries";
    private static final String QUERY_RESULT_URL = "/queries/%s/results";
    private static final String REFRESH_QUERY_URL = "/queries/%s/refresh";
    public static final String WIDGETS_URL = "/widgets";
    public static final String CREATE_USER_URL = "/users?no_invite";
    public static final String REFRESH_USER_TOKEN_URL = "/users/%s/regenerate_api_key";
    public static final String GET_ALL_USERS = "users?order=-created_at";
    public static final String ADD_MEMBER_TO_GROUP_URL = "/groups/%s/members";
    private static final String JOBS_URL = "jobs/";
    private static final String QUERY_RESULTS_URL = "query_results/";
    private static final String DATA_SOURCE_SCHEMA = "data_sources/%s/schema";
    private static final String JOB_STATUS_SUCCESS = "3";


    // WORK WITH REDASH DASHBOARDS SECTION

    public Map<String, Integer> getDashboardsList() {
        log.info("Fetching the list of dashboards");
        return fetchEntityMap(DASHBOARD_URL);
    }

    public Map<String, Integer> getQueriesList() {
        log.info("Fetching the list of queries");
        return fetchEntityMap(QUERIES_URL);
    }

    /**
     * Helper method to fetch a map of entities with names as keys and IDs as values from a given URL.
     *
     * @param url the endpoint URL to query
     * @return a map of entity names to their corresponding IDs
     */
    private Map<String, Integer> fetchEntityMap(String url) {
        List<Map<String, Object>> entityList =
                (List<Map<String, Object>>) redashClient.getRequest(url, "results");

        return entityList.stream()
                .collect(Collectors.toMap(
                        entity -> (String) entity.get("name"),
                        entity -> (Integer) entity.get("id")
                ));
    }

    public List<String> getDataSourcesList() {
        log.info("Fetching the list of data sources");
        return fetchListFromRedash(DATA_SOURCE_URL);
    }

    public List<String> getGroupsList() {
        log.info("Fetching the list of groups");
        return fetchListFromRedash(GROUPS_URL);
    }

    /**
     * Helper method to fetch a list of strings from Redash API.
     *
     * @param url the endpoint URL to query
     * @return a list of strings representing the requested field
     */
    private List<String> fetchListFromRedash(String url) {
        return (List<String>) redashClient.getRequest(url, "name");
    }

    public Response getDashboardByName(String name) {
        log.info("Fetching dashboard by name: {}", name);
        var list = getDashboardsList();
        return RestAssured.given(redashClient.getRequestSpecification(redash))
                .get(DASHBOARD_URL + "/" + list.get(name));
    }

    public Response getDashboardById(Integer id) {
        log.info("Fetching dashboard by id: {}", id);
        return RestAssured.given(redashClient.getRequestSpecification(redash))
                .get(DASHBOARD_URL + "/" + id);
    }

    public int getQueryIdByDashboardName(String dashboardName) {
        log.info("Fetching query ID for dashboard with name: {}", dashboardName);

        Response dashboardResponse = getDashboardByName(dashboardName);
        List<Map<String, Object>> widgets = dashboardResponse.jsonPath().getList("widgets");
        if (widgets == null || widgets.isEmpty()) {
            throw new IllegalStateException("No widgets found for dashboard: " + dashboardName);
        }

        Map<String, Object> visualization = (Map<String, Object>) widgets.get(0).get("visualization");
        if (visualization == null || !visualization.containsKey("id")) {
            throw new IllegalStateException("No visualization ID found in the first widget of dashboard: "
                    + dashboardName);
        }

        return (int) visualization.get("id");
    }

    public int createDashboardWithTextbox(String dashboardName) {
        log.info("Creating dashboard with name: {}", dashboardName);

        // Step 1: Create the dashboard
        CreateDashboardRequest createDashboardRequest = new CreateDashboardRequest(dashboardName);
        Map<String, Object> createDashboardResponse = (Map<String, Object>) redashClient
                .postRequest(DASHBOARD_URL, createDashboardRequest);
        Integer dashboardId = (Integer) createDashboardResponse.get("id");

        // Step 2: Add a textbox widget to the dashboard
        CreateTextboxRequest createTextboxRequest = CreateTextboxRequest.getRequest(dashboardName, dashboardId);
        redashClient.postRequest(WIDGETS_URL, createTextboxRequest);

        // Step 3: Publish the dashboard
        PublishDashboardRequest publishDashboardRequest = new PublishDashboardRequest(dashboardId, dashboardName,
                false);
        redashClient.postRequest(DASHBOARD_URL + "/" + dashboardId, publishDashboardRequest);

        return dashboardId;
    }

    public void deleteDashboardWithDashboardId(String dashboardName, Integer dashboardId) {
        log.info("Deleting dashboard with name: {} and ID: {}", dashboardName, dashboardId);
        String deleteUrl = DASHBOARD_URL + "/" + dashboardId;
        redashClient.deleteRequest(deleteUrl);
    }


    // WORK WITH REDASH QUERIES SECTION

    public String executeQuery(String id, IEntity entity) {
        log.info("Executing query with ID: {} and entity: {}", id, entity);

        // Step 1: Trigger the query and retrieve the job ID
        String jobId = (String) redashClient.getRequest(String.format(QUERY_RESULT_URL, id), "job.id");
        if (jobId == null) {
            throw new IllegalStateException("Failed to retrieve job ID for query: " + id);
        }

        // Step 2: Wait for the job to complete
        waitForJobCompletion(jobId);

        // Step 3: Retrieve the query result ID
        String queryResultId = (String) redashClient.getRequest(JOBS_URL + "/" + jobId, "job.query_result_id");
        if (queryResultId == null) {
            throw new IllegalStateException("Failed to retrieve query result ID for job: " + jobId);
        }

        // Step 4: Fetch the query result
        String queryResult = (String) redashClient.getRequest(QUERY_RESULTS_URL + "/" + queryResultId,
                "jquery_result.data");
        if (queryResult == null) {
            throw new IllegalStateException("Failed to retrieve query result data for ID: " + queryResultId);
        }

        return queryResult;
    }

    public Map<String, Object> executeQueryOnRedash(String queryId, Map<String, String> params, IEntity payload) {
        log.info("Executing query on Redash with ID: {} and payload: {}", queryId, payload);

        // Step 1: Refresh the query
        redashClient.emptyPostRequest(String.format(REFRESH_QUERY_URL, queryId), params);

        // Step 2: Execute the query
        Map<String, Object> queryResponse =
                (Map<String, Object>) redashClient.postRequest(String.format(QUERY_RESULT_URL, queryId), payload);
        if (queryResponse == null) {
            throw new IllegalStateException("Failed to execute query with ID: " + queryId);
        }

        return queryResponse;
    }

    void waitForJobCompletion(String jobId) {
        log.info("Waiting for query execution to complete for job ID: {}", jobId);

        await()
                .pollInterval(5, TimeUnit.SECONDS)
                .atMost(10, TimeUnit.MINUTES)
                .ignoreExceptions()
                .pollInSameThread()
                .untilAsserted(() -> {
                    String jobStatus = (String) redashClient.getRequest(JOBS_URL + "/" + jobId, "job.status");
                    assertThat(jobStatus).as("Job status for job ID: " + jobId).isEqualTo(JOB_STATUS_SUCCESS);
                });
    }


    // WORK WITH REDASH USER SECTION

    public RedashUser registerUser(RedashUser redashUser) {
        log.info("Registering user in Redash: {}", redashUser.getName());

        // If the user doesn't exist, create a new one
        if (!isUserExists(redashUser)) {
            Map<String, Object> response = (Map<String, Object>) redashClient.postRequest(CREATE_USER_URL, redashUser);
            String invitationLink = (String) response.get("invite_link");
            redashClient.postRequestToSpecificUrl(invitationLink, new RedashUserPassword("12342342353453"));
        }

        // Get the list of all users and retrieve the created user's ID
        List<Map<String, Object>> users = (List<Map<String, Object>>) redashClient.getRequest(GET_ALL_USERS, "results");
        Integer userId = users.stream()
                .filter(user -> redashUser.getName().equals(user.get("name")))
                .map(user -> (Integer) user.get("id"))
                .findFirst()
                .orElseThrow(()
                        -> new IllegalStateException("User not found after creation: " + redashUser.getName()));

        redashUser.setId(userId);

        // Refresh the access token for the user
        Map<String, Object> tokenResponse = (Map<String, Object>) redashClient.postRequest(
                String.format(REFRESH_USER_TOKEN_URL, userId), new RedashUserPassword());
        String apiKey = (String) tokenResponse.get("api_key");
        redashUser.setToken(apiKey);

        return redashUser;
    }

    public void assignRoleToUser(RedashUser redashUser, List<String> roles) {
        log.info("Assigning roles to user in Redash: {}", redashUser.getName());

        // Retrieve the list of all available groups (roles) in Redash
        List<Map<String, Object>> groups = (List<Map<String, Object>>) redashClient.getRequest(GROUPS_URL, "");

        // Find roles that exist in Redash and collect their IDs
        List<Integer> roleIds = groups.stream()
                .filter(group -> roles.contains(group.get("name")))
                .map(group -> (Integer) group.get("id"))
                .collect(Collectors.toList());

        // Create a request to assign roles to the user
        CreateRoleRequest request = new CreateRoleRequest(redashUser.getId());

        // Assign each role to the user
        roleIds.forEach(roleId -> {
            log.info("Assigning role ID {} to user ID {}", roleId, redashUser.getId());
            redashClient.postRequest(String.format(ADD_MEMBER_TO_GROUP_URL, roleId), request);
        });
    }

    private boolean isUserExists(RedashUser user) {
        log.info("Checking if user exists with name: {}", user.getName());
        List<Map<String, Object>> users = (List<Map<String, Object>>) redashClient
                .getRequest(GET_ALL_USERS, "results");

        return users.stream()
                .anyMatch(u -> user.getName().equals(u.get("name")));
    }


    // WORK WITH REDASH DATA SOURCES

    public Map<String, Integer> getDataSourceToId() {
        log.info("Fetching IDs for all data sources");

        // Get the list of data sources
        List<Map<String, Object>> list = (List<Map<String, Object>>) redashClient.getRequest(DATA_SOURCE_URL, "");

        // Convert the list of data sources into a map of name to id
        return list.stream()
                .collect(Collectors.toMap(
                        entity -> entity.get("name").toString(),
                        entity -> Integer.valueOf(entity.get("id").toString())
                ));
    }

    public List<String> getSchemaNameForDataSource(Integer dataSourceId) {
        log.info("Fetching schemas available for data source with ID {}", dataSourceId);

        // Get the list of schemas for the given data source
        List<Map<String, Object>> list = (List<Map<String, Object>>) redashClient.getRequest(
                String.format(DATA_SOURCE_SCHEMA, dataSourceId),
                "schema"
        );

        // Check if the list is not null and extract schema names
        if (list == null) {
            log.warn("No schemas found for data source with ID {}", dataSourceId);
            return Collections.emptyList();
        }

        return list.stream()
                .map(x -> x.get("name").toString())
                .collect(Collectors.toList());
    }
}
