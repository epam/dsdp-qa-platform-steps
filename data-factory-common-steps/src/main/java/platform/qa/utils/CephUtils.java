package platform.qa.utils;

import com.amazonaws.util.IOUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class CephUtils {
    public static final String PROCESS_ID_FOR_MULTIPLE_FILES = "4d3e7b19-21c8-49e4-8bae-2ad297566655";
    private static final String DASH = "/";

    private CephUtils() {
        throw new IllegalStateException("This is utility class!");
    }


    public static String getChecksumOfFile(File file) throws IOException {
        return DigestUtils.sha256Hex(IOUtils.toByteArray(new FileInputStream(file)));
    }

    public static String convertFileIdToStructuredKey(String id) {
        return getBuilder().append(id).append(DASH).append(id).toString();
    }

    public static String convertMultipleFileIdToStructuredKey(String id) {
        return getBuilder().append(PROCESS_ID_FOR_MULTIPLE_FILES).append(DASH).append(id).toString();
    }

    public static String convertFileIdToStructuredKey(String id, String processId) {
        return getBuilder().append(processId).append(DASH).append(id).toString();
    }

    private static StringBuilder getBuilder() {
        StringBuilder builder = new StringBuilder();
        return builder.append("process/");
    }

}