package aio.basic;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Package:aio.basic
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class Client {
    private final static String LOCAL_HOST = "localhost";
    private final static int DEFAULT_PORT = 8888;
    private AsynchronousSocketChannel client;

    public void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try {
            client = AsynchronousSocketChannel.open();
            Future<Void> connect = client.connect(new InetSocketAddress(LOCAL_HOST, DEFAULT_PORT));
            connect.get();
            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = read.readLine();

                byte[] buffer = input.getBytes();
                ByteBuffer buffers = ByteBuffer.wrap(buffer);
                // 将buffer从写模式转化为读模式
                buffers.flip();
                // 读取buffers到client中
                Future<Integer> writeResult = client.write(buffers);
                writeResult.get();
                // 将client数据写入到buffer
                Future<Integer> readResult = client.read(buffers);
                readResult.get();
                String echo = new String(buffers.array());
                buffers.clear();

                System.out.println(echo);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
