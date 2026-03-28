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

package platform.qa.pojo.redash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTextboxRequest implements IEntity {
    private TextboxOptions options;
    private String text;
    private long id;
    private int width;
    private int dashboard_id;
    private Integer visualization_id;

    public static CreateTextboxRequest getRequest(String text, int dashboardId) {
        return CreateTextboxRequest.builder()
                .text(text)
                .dashboard_id(dashboardId)
                .id(System.currentTimeMillis())
                .width(1)
                .options(TextboxOptions.getTextBoxOptions())
                .build();
    }
}