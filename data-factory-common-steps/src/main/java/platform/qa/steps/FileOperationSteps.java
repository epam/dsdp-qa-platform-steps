package platform.qa.steps;

import lombok.SneakyThrows;
import platform.qa.ceph.CephClient;
import platform.qa.pojo.consent.files.FileData;
import platform.qa.utils.CephUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class FileOperationSteps {
    private CephClient cephFileClient;

    public FileOperationSteps(CephClient cephFileClient) {
        this.cephFileClient = cephFileClient;
    }

    @SneakyThrows
    public void createFileInCephBucketWithMetadata(String bucketName, FileData fileData) {
        cephFileClient.saveFileInBucket(bucketName,
                fileData.getId(),
                new FileInputStream(fileData.getFile()),
                fileData.getMetadata()
        );
    }

    public void createFileInCephBucket(String bucketName, FileData fileData) {
        cephFileClient.saveFileInBucket(bucketName, fileData.getId(), fileData.getFile());
    }

    public void createMultipleFilesInCephBucket(String bucketName, List<FileData> filesData) {
        filesData.forEach(fileData ->
                cephFileClient.saveFileInBucket(
                        bucketName,
                        CephUtils.convertMultipleFileIdToStructuredKey(
                                fileData.getRealFileId()
                        ),
                        fileData.getFile())
        );
    }

    public File getFileFromBucket(String bucketName, String cephKey) {
        return cephFileClient.getFileFromBucket(bucketName, cephKey);
    }

    public ObjectMetadata getFileMetadataFromBucket(String bucketName, String cephKey) {
        return cephFileClient.getObjectMetadata(bucketName, cephKey);
    }

    public void deleteFileFromBucket(String bucketName, String cephKey) {
        cephFileClient.deleteFileFromBucket(bucketName, cephKey);
    }

    public void deleteMultipleFilesInCephBucket(String bucketName, List<FileData> filesData) {
        filesData.forEach(fileData -> cephFileClient.deleteFileFromBucket(bucketName, fileData.getId()));
    }
}
