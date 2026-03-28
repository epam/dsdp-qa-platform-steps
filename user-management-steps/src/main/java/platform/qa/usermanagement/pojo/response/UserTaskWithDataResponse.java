/*
 * Copyright 2024 EPAM Systems.
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

package platform.qa.usermanagement.pojo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTaskWithDataResponse {

    @JsonProperty("id")
    private String id;
    
    @JsonProperty("taskDefinitionKey")
    private String taskDefinitionKey;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("assignee")
    private String assignee;
    
    @JsonProperty("created")
    private LocalDateTime created;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("processInstanceId")
    private String processInstanceId;
    
    @JsonProperty("rootProcessInstanceId")
    private String rootProcessInstanceId;
    
    @JsonProperty("processDefinitionId")
    private String processDefinitionId;
    
    @JsonProperty("processDefinitionName")
    private String processDefinitionName;
    
    @JsonProperty("formKey")
    private String formKey;
    
    @JsonProperty("esign")
    private boolean eSign;
    
    @JsonProperty("payment")
    private boolean payment;
    
    @JsonProperty("suspended")
    private boolean suspended;
    
    @JsonProperty("formVariables")
    private Map<String, Object> formVariables;
    
    @JsonProperty("signatureValidationPack")
    private Set<Object> signatureValidationPack;
    
    @JsonProperty("data")
    private Map<String, Object> data;
}

