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

package platform.qa.entities;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Deployment {
  public List<Link> links;
  public String id;
  public String name;
  public String source;
  public String deploymentTime;
  public String tenantId;
  public DeployedProcessDefinitions deployedProcessDefinitions;
  public Object deployedCaseDefinitions;
  public Object deployedDecisionDefinitions;
  public Object deployedDecisionRequirementsDefinitions;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Link {
    public String method;
    public String href;
    public String rel;
  }


  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class DeployedProcessDefinitions {
    @JsonIgnore private Map<String, ProcessDefinitionData> processDefinitionsData = new HashMap<>();

    @JsonAnyGetter
    public Map<String, ProcessDefinitionData> getProcessDefinitionsData() {
      return new HashMap<>(this.processDefinitionsData);
    }

    @JsonAnySetter
    public void setProcessDefinitionsData(String name, ProcessDefinitionData value) {
      this.processDefinitionsData.put(name, value);
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ProcessDefinitionData {
    public String id;
    public String key;
    public String category;
    public String description;
    public String name;
    public Integer version;
    public String resource;
    public String deploymentId;
    public String diagram;
    public Boolean suspended;
    public Object tenantId;
    public Object versionTag;
  }
}
