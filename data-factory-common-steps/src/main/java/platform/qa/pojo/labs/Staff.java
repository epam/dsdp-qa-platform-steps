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

package platform.qa.pojo.labs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;
import platform.qa.pojo.common.FileData;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Staff implements IEntity {
    private String specializationEndDate;
    private String laboratoryId;
    private String dismissalDate;
    private String staffStatusId;
    private String contractEndDate;
    private FileData hireStaffFile;
    private String[] researches;
    private String education;
    private boolean hygienistFlag;
    private String specializationDate;
    private String fullName;
    private FileData ordersFile;
    private String staffId;
    private boolean fixedTermContractFlag;
    private boolean fullTimeFlag;
    private double salary;
    private FileData hygienistCertificateFile;
    private String seniority;
}
