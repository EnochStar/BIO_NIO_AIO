package easy_web.connector;

import java.io.*;

/**
 * Package:easy_web.connector
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class Response {
    Request request;
    OutputStream outputStream;
    private static final int BUFFER_SIZE = 1024;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
    // 封装write 根据情况返回 404或正确页面
    public void sendStaticResource() throws IOException {
        File file = new File(ConnectorUtils.WEB_ROOT,request.getUri());
        try {
            write(file,HttpStatus.SC_OK);
        } catch (IOException e) {
            write(new File(ConnectorUtils.WEB_ROOT,"404.html"),HttpStatus.SC_NOT_FOUNT);
        }
    }

    // 读取文件内容 同时返回状态 到output中
    public void write(File resource, HttpStatus status) throws IOException {
        try(FileInputStream fis = new FileInputStream(resource)) {
            outputStream.write(ConnectorUtils.renderStatus(status).getBytes());
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = fis.read(buffer,0,BUFFER_SIZE)) != -1) {
                outputStream.write(buffer,0,len);
            }
        }
    }
}
