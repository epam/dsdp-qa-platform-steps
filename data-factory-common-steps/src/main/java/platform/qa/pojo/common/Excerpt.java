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

package platform.qa.pojo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Excerpt {
    private String id;
    private String status;
    private String status_details;
    private String keycloak_id;
    private String checksum;
    private String excerpt_key;
    private String created_at;
    private String updated_at;
    private String signature_required;
    private String x_source_system;
    private String x_source_application;
    private String x_source_business_process;
    private String x_source_business_activity;
    private String x_digital_signature;
    private String x_digital_signature_derived;
}
