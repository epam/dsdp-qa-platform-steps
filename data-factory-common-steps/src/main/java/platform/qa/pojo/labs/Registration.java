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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;
import platform.qa.pojo.common.FileData;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Registration implements IEntity {
    private String solutionTypeId;
    private String healthMinistryLetterDate;
    private String[] factors;
    private String laboratoryId;
    private String solutionDate;
    private String healthMinistryLetterNumber;
    private String registrationSource;
    private String applicationTypeId;
    private String registrationId;
    private String createdDate;
    private String notes;
    private String acceptedBy;
    private String letterNo;
    private String certifiedBy;
    private String registrationNo;
    private String receivedBy;
    private String letterDate;
    private String[] refusalReasons;
    private String exclusionOrderNo;
    private String exclusionOrderDate;
    private FileData zvtFile;
    private FileData zvtCertificateFile;
    private FileData reagentAvailabilityFile;
    private FileData reagentFile;
}