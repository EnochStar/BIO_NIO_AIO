package nio.chatRoom;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
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
public class Client {
    private SocketChannel socketChannel;
    private ByteBuffer rBuffer = ByteBuffer.allocate(allocateBuffer);
    private ByteBuffer wBuffer = ByteBuffer.allocate(allocateBuffer);
    private Selector selector;
    private int port;
    private String host;

    private static final int DEFAULT_PORT = 8888;
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int allocateBuffer = 1024;
    private static final String QUIT = "quit";
    private Charset charset = Charset.forName("UTF-8");

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public Client() {
        this(DEFAULT_PORT,DEFAULT_HOST);
    }

    public void start() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(host,port));

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
        } catch (ClosedSelectorException e) {

        }
        finally {
            close(selector);
        }
    }

    public void handles(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isConnectable()) {
            SocketChannel client = (SocketChannel) selectionKey.channel();
            // 连接服务器
            if (client.isConnectionPending()) {
                client.finishConnect();
                // 处理输入
                new Thread(new UserInputHandler(this)).start();
            }
            client.register(selector,SelectionKey.OP_READ);
        }else if (selectionKey.isReadable()) {
            SocketChannel client = (SocketChannel) selectionKey.channel();
            String msg = receive(client);
            if(msg.isEmpty()) {
                close(selector);
            }else {
                System.out.println(msg);
            }
        }
    }
    // 写入当前socket 使其可读
    public void send(String msg) throws IOException {
        if (msg.isEmpty()) {
            return;
        }

        wBuffer.clear();
        wBuffer.put(charset.encode(msg));
        wBuffer.flip();
        while (wBuffer.hasRemaining()) {
            socketChannel.write(wBuffer);
        }
        if (readyTOQuit(msg)) {
            close(selector);
        }
    }
    // 接收用户输入
    public String receive(SocketChannel client) throws IOException {
        rBuffer.clear();
        while (client.read(rBuffer) > 0);
        rBuffer.flip();
        return String.valueOf(charset.decode(rBuffer));
    }

    public boolean readyTOQuit(String msg) {
        return QUIT.equals(msg);
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
        Client client = new Client();
        client.start();
    }
}
