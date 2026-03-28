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

package platform.qa;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.log4j.Log4j2;
import platform.qa.ceph.CephClient;
import platform.qa.entities.Ceph;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import com.amazonaws.services.s3.model.ObjectMetadata;

/**
 * Steps to work with ceph client
 */
@Log4j2
public class CephClientSteps {
    private final CephClient lowCodeCeph;
    private final String bucketName;

    public CephClientSteps(Ceph ceph) {
        lowCodeCeph = new CephClient(ceph);
        bucketName = lowCodeCeph.getBuckets().stream().findFirst().orElseThrow().getName();
    }

    public void deleteAllFilesFromCeph() {
        log.info("Deleting all files from Ceph");
        lowCodeCeph.getListOfFilesFromBucket(bucketName)
                .forEach(cephKey -> lowCodeCeph.deleteFileFromBucket(bucketName, cephKey));
    }

    public List<String> checkFilesExistInCeph(List<String> filesToCheck, String processInstanceId) {
        log.info("Checking files in Ceph");
        List<String> allBucketFiles = lowCodeCeph.getListOfFilesFromBucket(bucketName);
        List<String> fileNames = new ArrayList<>();
        List<String> fileIds = new ArrayList<>();

        allBucketFiles.stream().filter(cephKey -> cephKey.contains(processInstanceId))
                .forEach(fileId -> {
                    fileIds.add(fileId);
                    ObjectMetadata objectMetadata = lowCodeCeph.getObjectMetadata(bucketName, fileId);
                    fileNames.add(objectMetadata.getUserMetadata().get("filename"));
                });

        assertThat(fileNames)
                .as("No corresponding files found in Ceph")
                .containsExactlyInAnyOrderElementsOf(filesToCheck);
        return fileIds;
    }

    public void checkFilesDoNotExistInCeph(List<String> fileIds) {
        log.info("Checking that files do not exist in Ceph");
        assertThat(lowCodeCeph.getListOfFilesFromBucket(bucketName))
                .as("Found files that should not exist in Ceph")
                .doesNotContainAnyElementsOf(fileIds);
    }

    public Map uploadFileToCeph(File file, String processId) throws IOException {
        log.info("Uploading file to Ceph");
        String fileId = UUID.randomUUID().toString();
        String cephKey = "process/" + processId + "/" + fileId;
        String fileName = file.getName();

        byte[] data = IOUtils.toByteArray(new FileInputStream(file));
        String checkSum = DigestUtils.sha256Hex(data);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.addUserMetadata("id", fileId);
        objectMetadata.addUserMetadata("checksum", checkSum);
        objectMetadata.addUserMetadata("filename", fileName);

        lowCodeCeph.saveFileInBucket(bucketName, cephKey, new ByteArrayInputStream(data), objectMetadata);

        return Map.of("cephkey", cephKey, "checksum", checkSum, "filename", fileName);
    }
}
