package aio.chat;



import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Package:aio.chat
 * Description:
 * 聊天室 服务器端
 * @author:鲍嘉鑫
 */
public class Server {
    private static final String DEFAULT_LOCAL_HOST = "localhost";
    private static final int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFER = 1024;
    private static final int THREADPOOL_SIZE = 8;

    private AsynchronousChannelGroup asynchronousChannelGroup;
    private Charset charSet = Charset.forName("UTF-8");
    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    private int port;
    private List<ClientHandler> clientHandlerList;

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
        clientHandlerList = new ArrayList<>();
    }

    public void start() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
            asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(executorService);

            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(asynchronousChannelGroup);
            AcceptHandler acceptHandler = new AcceptHandler();
            System.out.println("服务器启动成功，监听地址：" + asynchronousServerSocketChannel.getLocalAddress());
            while (true) {
                asynchronousServerSocketChannel.accept(null,acceptHandler);
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,Object> {

        @Override
        public void completed(AsynchronousSocketChannel client, Object attachment) {
            if (asynchronousServerSocketChannel.isOpen()) {
                asynchronousServerSocketChannel.accept(null,this);
            }
            if (client != null && client.isOpen()) {
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER);
                ClientHandler clientHandler = new ClientHandler(client);
                clientHandlerList.add(clientHandler);
                // 读取客户端发送的数据 到buffer中
                client.read(buffer,buffer,clientHandler);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            System.out.println("连接失败：" + exc);
        }
    }

    private class ClientHandler implements CompletionHandler<Integer, Object> {

        private AsynchronousSocketChannel client;

        public ClientHandler(AsynchronousSocketChannel client) {
            this.client = client;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            ByteBuffer buffer = (ByteBuffer) attachment;
            // 如果是read 那么需要继续从buffer中读取，而如果是write那么直接转发出去就可以了 不再需要buffer
            if (attachment != null) {
                // 当读取到的值小于0 说明客户端发送异常 即可放弃该客户端
                if (result <= 0 ) {
                    clientHandlerList.remove(this);
                }else{
                    buffer.flip();
                    // 对buffer中的内容进行解码 发送出去
                    String fwd = receive(buffer);
                    System.out.println(getClientName(client) + "发送消息:" + fwd);
                    forwardMsg(fwd,this,clientHandlerList);
                    buffer.clear();
                    if (readyToQuit(fwd)) {
                        clientHandlerList.remove(this);
                    }
                }
            }else{
                client.read(buffer,buffer,this);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }

        private synchronized void forwardMsg(String msg,ClientHandler source,List<ClientHandler> clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.equals(source)) {
                    continue;
                }
                ByteBuffer byteBuffer = charSet.encode(getClientName(clientHandler.client) + msg);

                clientHandler.client.write(byteBuffer,null,clientHandler);
            }
        }

        private String receive(ByteBuffer buffer) {
            CharBuffer charBuffer = charSet.decode(buffer);
            return String.valueOf(charBuffer);
        }

        private String getClientName(AsynchronousSocketChannel client) {
            int clientPort = -1;
            try {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) client.getRemoteAddress();
                clientPort = inetSocketAddress.getPort();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "客户端[" + clientPort + "]";
        }
    }

    private boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    private void close(Closeable closeable) {
        if (asynchronousServerSocketChannel != null) {
            try {
                asynchronousServerSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
