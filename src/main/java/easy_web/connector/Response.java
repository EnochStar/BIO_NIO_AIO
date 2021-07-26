package easy_web.connector;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.*;
import java.util.Locale;

/**
 * Package:easy_web.connector
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class Response implements ServletResponse {
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

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        // printWriter写入数据自动rush，println生效，print不生效
        return new PrintWriter(outputStream,true);
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public void setContentLength(int len) {

    }

    @Override
    public void setContentLengthLong(long length) {

    }

    @Override
    public void setContentType(String type) {

    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
