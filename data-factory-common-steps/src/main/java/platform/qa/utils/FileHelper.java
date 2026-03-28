package platform.qa.utils;

import com.amazonaws.services.s3.model.ObjectMetadata;
import platform.qa.pojo.consent.files.FileData;

import java.io.File;
import java.util.UUID;

public final class FileHelper {

    private FileHelper() {
        throw new IllegalStateException("This is utility class!");
    }

    public static FileData getFileData(String path) {
        String id = UUID.randomUUID().toString();

        File file = new File(path);

        return FileData.builder()
                .realFileId(id)
                .id(CephUtils.convertFileIdToStructuredKey(id))
                .metadata(new ObjectMetadata())
                .file(file)
                .build();
    }
}
