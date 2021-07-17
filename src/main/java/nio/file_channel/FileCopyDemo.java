package nio.file_channel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Package:nio.file_channel
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class FileCopyDemo {

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FileCopyRunner noBufferStreamCopy;
        FileCopyRunner bufferedStreamCopy;
        FileCopyRunner nioBufferCopy;
        FileCopyRunner nioTransferCopy;

        nioBufferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileInputStream fis = null;
                FileOutputStream fos = null;
                try {
                    fis = new FileInputStream(source);
                    fos = new FileOutputStream(target);
                    int res;
                    while ((res = fis.read()) != -1 ) {
                        fos.write(res);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fis);
                    close(fos);
                }
            }
        };

        bufferedStreamCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                InputStream fis = null;
                OutputStream fos = null;
                try {
                    fis = new BufferedInputStream(new FileInputStream(source));
                    fos = new BufferedOutputStream(new FileOutputStream(target));
                    byte[] buffer = new byte[1024];
                    int res;
                    while ((res = fis.read(buffer)) != -1) {
                        fos.write(buffer,0,res);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fis);
                    close(fos);
                }
            }
        };

        nioBufferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel fis = null;
                FileChannel fos = null;
                try{
                    fis = new FileInputStream(source).getChannel();
                    fos = new FileOutputStream(target).getChannel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int res;
                    while ((res=fis.read(byteBuffer)) != -1) {
                        byteBuffer.flip();
                        while (byteBuffer.hasRemaining()) {
                            fos.write(byteBuffer);
                        }
                        byteBuffer.clear();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    close(fis);
                    close(fos);
                }
            }
        };

        nioTransferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel fis = null;
                FileChannel fos = null;
                try {
                    fis = new FileInputStream(source).getChannel();
                    fos = new FileOutputStream(target).getChannel();
                    long size = fis.size();
                    long transferCount = 0;
                    while (transferCount < size) {
                        transferCount += fis.transferTo(0,size,fos);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fis);
                    close(fos);
                }
            }
        };
    }

}
interface FileCopyRunner {
    void copyFile(File source, File target);
}
