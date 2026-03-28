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
public enum Permission {
    NONE("NONE"),
    ALL("ALL"),
    READ("READ"),
    UPDATE("UPDATE"),
    CREATE("CREATE"),
    DELETE("DELETE"),
    ACCESS("ACCESS"),
    READ_TASK("READ_TASK"),
    UPDATE_TASK("UPDATE_TASK"),
    TASK_WORK("TASK_WORK"),
    TASK_ASSIGN("TASK_ASSIGN"),
    CREATE_INSTANCE("CREATE_INSTANCE"),
    READ_INSTANCE("READ_INSTANCE"),
    UPDATE_INSTANCE("UPDATE_INSTANCE"),
    MIGRATE_INSTANCE("MIGRATE_INSTANCE"),
    DELETE_INSTANCE("DELETE_INSTANCE"),
    READ_HISTORY("READ_HISTORY"),
    DELETE_HISTORY("DELETE_HISTORY");


    @Getter
    private final String permission;
}
