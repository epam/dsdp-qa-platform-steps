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

package platform.qa.steps;

import lombok.extern.log4j.Log4j2;
import platform.qa.api.AuthorizationApi;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.entities.auth.AuthorizationRequest;
import platform.qa.enums.AuthorizationType;
import platform.qa.enums.Permission;
import platform.qa.enums.ResourceType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Steps to work with bpms authorizations
 */
@Log4j2
public class BpmsAuthorizationSteps {
    private Service bpmsService;

    public BpmsAuthorizationSteps(Service bpmsService) {
        this.bpmsService = bpmsService;
    }

    public List<String> createGroupAuthorization(String groupId, String processKey, ResourceType... resourceTypes) {
        log.info("Створити авторизацію для групи:{}, процесу:{} та списку ресурсів:{}",
                groupId, processKey, resourceTypes);
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .type(AuthorizationType.AUTH_TYPE_GRANT.getType())
                .permissions(List.of(Permission.READ.getPermission(), Permission.CREATE_INSTANCE.getPermission()))
                .groupId(groupId)
                .resourceId(processKey)
                .build();
        return createBpmsAuthorizations(authorizationRequest, resourceTypes);
    }

    public List<String> createGroupAuthorization(String groupId, ResourceType resourceType, String processKey,
            Permission... permissions) {
        log.info("Створити авторизацію для групи:{}, процесу:{}, ресурсу:{} з правами:{}",
                groupId, resourceType, processKey, permissions);
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .type(AuthorizationType.AUTH_TYPE_GRANT.getType())
                .permissions(Arrays.stream(permissions).map(Permission::getPermission).collect(Collectors.toList()))
                .groupId(groupId)
                .resourceId(processKey)
                .build();
        return createBpmsAuthorizations(authorizationRequest, resourceType);
    }

    public List<String> createUserAuthorization(User user, String processKey, ResourceType resourceType,
            Permission... permissions) {
        log.info("Створити авторизацію для користувача:{}, процесу:{}, ресурсу:{} з "
                + "правами:{}", user, processKey, resourceType, permissions);
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .type(AuthorizationType.AUTH_TYPE_GRANT.getType())
                .permissions(Arrays.stream(permissions).map(Permission::getPermission).collect(Collectors.toList()))
                .userId(user.getLogin())
                .groupId(null)
                .resourceId(processKey)
                .build();
        return createBpmsAuthorizations(authorizationRequest, resourceType);
    }

    public List<String> createUserAuthorization(User user) {
        log.info("Створити авторизації до всіх ресурсів з усіма правами для користувача:{}", user);
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .type(AuthorizationType.AUTH_TYPE_GRANT.getType())
                .permissions(Collections.singletonList(Permission.ALL.getPermission()))
                .userId(user.getLogin())
                .groupId(null)
                .resourceId("*")
                .build();
        return createBpmsAuthorizations(authorizationRequest, ResourceType.PROCESS_DEFINITION,
                ResourceType.PROCESS_INSTANCE, ResourceType.DECISION_DEFINITION);
    }

    public void deleteAuthorization(String authorizationId) {
        log.info("Видалити авторизацію з id:{}", authorizationId);
        getBpmsAuthApi().deleteAuthorization(authorizationId);
    }

    public void updateGroupAuthorization(String groupId, String processKey,
            List<Permission> permissions, ResourceType resourceType) {
        log.info("Оновити авторизації для групи:{}, ресурсу:{}, процесу:{} з дозволами:{}",
                groupId, resourceType, processKey, permissions);
        List<Map> authorizations = getBpmsAuthApi().getProcessDefinitionAuth(groupId, processKey,
                resourceType.getType());

        if (authorizations.isEmpty()) return;
        String authorizationId = String.valueOf(authorizations.get(0).get("id"));

        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .permissions(getConvertedPermissions(permissions))
                .groupId(groupId)
                .resourceType(resourceType.getType())
                .resourceId(processKey)
                .build();

        getBpmsAuthApi().updateAuthorization(authorizationId, authorizationRequest);
    }

    public void updateGroupAuthorizationForProcesses(String groupId, List<String> processKeys,
            List<Permission> permissions, ResourceType resourceType) {
        log.info("Оновити авторизації для групи:{}, процесів {} з дозволами:{}", groupId, processKeys, permissions);
        processKeys.forEach(processKey -> updateGroupAuthorization(groupId, processKey, permissions, resourceType));
    }

    public List<Map> getGroupAuthorization(String group) {
        log.info("Пошук всіх авторизацій з групою {}", group);
        return getBpmsAuthApi().getAuthorizationsByParam("groupIdIn", group);
    }

    public void deleteGroupAuthorizations(String group) {
        log.info("Видалення авторизацій з групою {}", group);
        getGroupAuthorization(group).stream()
                .parallel().forEach(auth -> getBpmsAuthApi().deleteAuthorization((String) auth.get("id")));
    }

    public void deleteUserAuthorizations(User... users) {
        log.info("Видалити всі авторизації для користувачів зі списку {}", users);
        String userIds = Arrays.stream(users).map(User::getLogin).collect(Collectors.joining(","));
        getBpmsAuthApi().getAuthorizationsByParam("userIdIn", userIds)
                .stream().parallel().forEach(auth -> getBpmsAuthApi().deleteAuthorization((String) auth.get("id")));
    }

    private List<String> getConvertedPermissions(List<Permission> permissions) {
        return permissions.stream()
                .map(Permission::getPermission)
                .collect(Collectors.toList());
    }

    private List<String> createBpmsAuthorizations(AuthorizationRequest authorizationRequest,
            ResourceType... resourceTypes) {
        return Arrays.stream(resourceTypes).map(resourceType -> {
            authorizationRequest.setResourceType(resourceType.getType());
            return getCreatedOrUpdatedAuthorizationId(authorizationRequest);
        }).collect(Collectors.toList());
    }

    private String getCreatedOrUpdatedAuthorizationId(AuthorizationRequest authorizationRequest) {
        var authorization = getUserAuthByResourceAndPermissions(authorizationRequest);
        if (authorization.isPresent()) {
            String authorizationId = authorization.get().get("id").toString();
            updateAuthorization(authorizationRequest, authorizationId);
            return authorizationId;
        } else {
            return getBpmsAuthApi().createAuthorization(authorizationRequest);
        }
    }

    private Optional<Map> getUserAuthByResourceAndPermissions(AuthorizationRequest authorizationRequest) {
        var authorizations = getBpmsAuthApi().getAuthorizationsByResource(authorizationRequest);
        return authorizations.stream()
                .filter(authorization -> ((List) authorization.get("permissions"))
                        .stream().anyMatch(permission -> authorizationRequest.getPermissions().contains(permission)))
                .findFirst();
    }

    private void updateAuthorization(AuthorizationRequest authorizationRequest, String authorizationId) {
        AuthorizationRequest updateAuthorizationRequest = AuthorizationRequest.builder()
                .permissions(authorizationRequest.getPermissions())
                .userId(authorizationRequest.getUserId())
                .groupId(authorizationRequest.getGroupId())
                .resourceType(authorizationRequest.getResourceType())
                .resourceId(authorizationRequest.getResourceId())
                .build();
        getBpmsAuthApi().updateAuthorization(authorizationId, updateAuthorizationRequest);
    }

    public AuthorizationApi getBpmsAuthApi() {
        return new AuthorizationApi(this.bpmsService);
    }

}
