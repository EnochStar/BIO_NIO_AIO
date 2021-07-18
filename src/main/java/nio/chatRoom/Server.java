package nio.chatRoom;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * Package:nio.chatRoom
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class Server {
    private static final int DEFAULT_PORT = 8888;
    private static final int bufferLocate = 1024;
    private static final String QUIT = "quit";
    private Charset charset = Charset.forName("UTF-8");

    private ServerSocketChannel serverChannel;
    private ByteBuffer rBuffer = ByteBuffer.allocate(bufferLocate);
    private ByteBuffer wBuffer = ByteBuffer.allocate(bufferLocate);
    private Selector selector;
    private int port;

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port));

            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("启动服务器，监听端口：" + port + "...");

            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    handles(selectionKey);
                }
                selectionKeys.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(selector);
        }
    }

    public void handles(SelectionKey selectionKey) throws IOException {
        // 有客户端连入
        if (selectionKey.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
            SocketChannel accept = server.accept();
            accept.configureBlocking(false);
            accept.register(selector,SelectionKey.OP_READ);
            System.out.println("客户端：" + accept.getRemoteAddress() + "接入服务器");
            // 有可读事件
        } else if (selectionKey.isReadable()) {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            String fwdMsg = readBuffer(channel);
            if (fwdMsg.isEmpty()) {
                selectionKey.cancel();
                selector.wakeup();
            }else {
                System.out.println(channel.getRemoteAddress() + ":" + fwdMsg);
                forwardMsg(channel,fwdMsg);
                if (fwdMsg.equals(QUIT)) {
                    selectionKey.cancel();
                    selector.wakeup();
                    System.out.println(channel.getRemoteAddress() + ":" + "已经断开");
                }
            }
        }
    }

    public String readBuffer(SocketChannel client) {
        try {
            rBuffer.clear();
            while (client.read(rBuffer) > 0);
            rBuffer.flip();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("解码 ====> "+charset.decode(rBuffer)+" <====");
        return String.valueOf(charset.decode(rBuffer));
    }


    public void forwardMsg(SocketChannel client,String msg) throws IOException {
        // 不能是服务器 且发送的目标不能是当前client，同时必须是isValid
        for (SelectionKey selectionKey : selector.keys()) {
            Channel channel = selectionKey.channel();
            if (channel instanceof ServerSocketChannel)
                continue;
            if (selectionKey.isValid() && !client.equals(channel)) {
                wBuffer.clear();
//                System.out.println("编码 ====>" +charset.encode(msg)+"<====");
                wBuffer.put(charset.encode(client.getRemoteAddress() + ":" + msg));
                wBuffer.flip();
                while (wBuffer.hasRemaining()) {
                    ((SocketChannel)channel).write(wBuffer);
                }
            }
        }
    }


    public void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
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
