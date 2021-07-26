package easy_web.connector;

import easy_web.processor.StaticProcessor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Package:easy_web.connector
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class Connector implements Runnable {
    private static final int DEFAULT_PORT = 9999;
    private ServerSocket server;
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
            server = new ServerSocket(port);
            System.out.println("启动服务器，监听端口：" + port);
            while (true) {
                Socket client = server.accept();
                InputStream input = client.getInputStream();
                OutputStream output = client.getOutputStream();

                Request request = new Request(input);
                request.parse();

                Response response = new Response(output);
                response.setRequest(request);

                StaticProcessor staticProcessor = new StaticProcessor();
                staticProcessor.processor(request,response);

                close(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(server);
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
