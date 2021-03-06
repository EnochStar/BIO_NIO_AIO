package easy_web.utils;

import easy_web.connector.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Package:easy_web.utils
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class TestUtils {

    public static Request createRequest(String requestStr) {
        InputStream inputStream = new ByteArrayInputStream(requestStr.getBytes());
        Request request = new Request(inputStream);
        request.parse();
        return request;
    }

    public static String readFileToString(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }
}
