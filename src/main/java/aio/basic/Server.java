package aio.basic;

import com.sun.org.apache.xml.internal.security.utils.UnsyncByteArrayOutputStream;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * Package:aio.basic
 * Description:
 * 实现回声应答的功能，即客户端发来消息给服务端，服务端将该消息返回给该客户端
 * @author:鲍嘉鑫
 */
public class Server {
    private AsynchronousServerSocketChannel serverChannel;
    private final static String LOCAL_HOST = "localhost";
    private final static int DEFAULT_PORT = 8888;
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
            serverChannel = AsynchronousServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(LOCAL_HOST,DEFAULT_PORT));
            System.out.println("启动服务器，监听端口：" + DEFAULT_PORT);
            while (true) {
                serverChannel.accept(null,new AcceptHandler());
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(serverChannel);
        }
    }
    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

        public AcceptHandler() {
        }


        @Override
        public void completed(AsynchronousSocketChannel result, Object attachment) {
            if(serverChannel.isOpen()) {
                serverChannel.accept(null,this);
            }
            if (result != null && result.isOpen()){

                Map<String,Object> info = new HashMap<>();
                info.put("type","read");
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                info.put("buffer",buffer);
                ClientHandler handler = new ClientHandler(result);

                result.read(buffer,info,handler);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            // 自行设置
        }
    }

    private class ClientHandler implements CompletionHandler<Integer,Object>{
        private AsynchronousSocketChannel client;

        public ClientHandler(AsynchronousSocketChannel client) {
            this.client = client;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            Map<String,Object> info = (Map<String, Object>) attachment;
            if (info.get("type").equals("read")) {
                ByteBuffer buffer = (ByteBuffer) info.get("buffer");
                buffer.flip();
                info.put("type","write");
                client.write(buffer,info,this);
                buffer.clear();
            }else{
//                ByteBuffer buffer = ByteBuffer.allocate(1024);
                ByteBuffer buffer = (ByteBuffer) info.get("buffer");
                info.put("type","read");
                client.write(buffer,info,this);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}
