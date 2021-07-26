package easy_web.connector;

import easy_web.processor.ServletProcessor;
import easy_web.processor.StaticProcessor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.*;
import java.util.Set;

/**
 * Package:easy_web.connector
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class Connector implements Runnable {
    private static final int DEFAULT_PORT = 9999;
    private ServerSocketChannel server;
    private Selector selector;
    private int port;

    public Connector() {
        this(DEFAULT_PORT);
    }

    public Connector(int port) {
        this.port = port;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(port));
            System.out.println("启动服务器，监听端口：" + port);

            selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
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
            close(server);
        }
    }

    public void handles(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
            SocketChannel accept = server.accept();
            accept.configureBlocking(false);
            accept.register(selector,SelectionKey.OP_READ);
        }else{
            SocketChannel client = (SocketChannel) selectionKey.channel();
            selectionKey.cancel();
            client.configureBlocking(true);

            Socket socket = client.socket();
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            Request request = new Request(inputStream);
            request.parse();

            Response response = new Response(outputStream);
            response.setRequest(request);
            if (request.getUri().startsWith("/servlet/")) {
                ServletProcessor servletProcessor = new ServletProcessor();
                servletProcessor.processor(request,response);
            }else{
                StaticProcessor staticProcessor = new StaticProcessor();
                staticProcessor.processor(request,response);
            }
            close(client);
        }
    }

    private void close(Closeable closeable) {
        while (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
