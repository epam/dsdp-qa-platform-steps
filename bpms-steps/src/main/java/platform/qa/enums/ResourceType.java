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

package platform.qa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResourceType {
    APPLICATION(0),
    AUTHORIZATION(4),
    BATCH(13),
    DECISION_DEFINITION(10),
    DECISION_REQUIREMENTS_DEFINITION(14),
    DEPLOYMENT(9),
    FILTER(5),
    GROUP(2),
    GROUP_MEMBERSHIP(3),
    PROCESS_DEFINITION(6),
    PROCESS_INSTANCE(8),
    TASK(7),
    TENANT(11),
    TENANT_MEMBERSHIP(12),
    USER(1);

    @Getter
    private final Integer type;
}
