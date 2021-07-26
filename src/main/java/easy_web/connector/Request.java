package easy_web.connector;

import java.io.IOException;
import java.io.InputStream;

/**
 * Package:easy_web.connector
 * Description:
 *
 * @author:鲍嘉鑫
 */

/*
 GET /index.html HTTP/1.1
    Host: ......
    Connection:.....
    .....
 */
public class Request {
    private static final int BUFFER_SIZE = 1024;

    private InputStream input;
    private String uri;

    public Request(InputStream input) {
        this.input = input;
    }

    public String getUri() {
        return uri;
    }

    // 读取inputstream 并将uri返回
    public void parse() {
        int length = 0;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            length = input.read(buffer);
            StringBuilder sb = new StringBuilder();
            for (int j = 0;j < length;j++) {
                sb.append((char) buffer[j]);
            }
            uri = parseUri(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 截取uri
    private String parseUri(String request) {
        int index = request.indexOf(' ');
        if (index != -1) {
            int index2 = request.indexOf(' ',index + 1);
            if (index2 > index) {
                return request.substring(index + 1,index2);
            }
        }
        return "";
    }
}
