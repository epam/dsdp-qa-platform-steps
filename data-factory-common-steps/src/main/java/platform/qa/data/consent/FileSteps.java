package platform.qa.data.consent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpStatus;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.IEntity;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.pojo.consent.files.FileData;
import platform.qa.pojo.consent.files.ScanCopy;
import platform.qa.rest.RestApiClient;
import platform.qa.utils.CephUtils;
import platform.qa.utils.FileHelper;

public class FileSteps {

  private Service dataFactory;
  private Service digitalSignature;

  private List<Redis> redis;

  public FileSteps(Service dataFactory, Service digitalSignature, List<Redis> redis) {
    this.dataFactory = dataFactory;
    this.digitalSignature = digitalSignature;
    this.redis = new ArrayList<>(redis);
  }

  public FileData getFileData(String filePath) {
    return FileHelper.getFileData(filePath);
  }

  public ScanCopy getScanCopy(FileData fileData) throws IOException {
    return ScanCopy.builder()
        .checksum(CephUtils.getChecksumOfFile(fileData.getFile()))
        .id(fileData.getRealFileId())
        .build();
  }

  public <T extends IEntity> String createFile(T payload, String path, String processInstanceId) {
    String signature =
        new SignatureSteps(dataFactory, digitalSignature, redis).signRequest(payload);

    return new RestApiClient(dataFactory, signature, processInstanceId)
        .post(payload, path)
        .then()
        .statusCode(HttpStatus.SC_CREATED)
        .extract()
        .jsonPath()
        .getString("id");
  }
}
