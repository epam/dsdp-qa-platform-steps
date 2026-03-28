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

package platform.qa.operators.steps.entities;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import platform.qa.git.entity.Status;

@Data
@Builder(builderMethodName = "hiddenBuilder")
@ToString
public class FilterGerritChanges {
    private String project;
    private String numberMergeRequests;
    private Status status;

    @Builder.Default
    private int countLastChanges = 5;

    public static FilterGerritChangesBuilder builder(String project, String numberMergeRequests, Status status) {
        return hiddenBuilder()
                .project(project)
                .numberMergeRequests(numberMergeRequests)
                .status(status);
    }
}
