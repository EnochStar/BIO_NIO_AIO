package bio.chat.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Package:bio.chat.server
 * Description:
 * 用于接收用户连接
 * @author:鲍嘉鑫
 */
public class Server {

    private static final int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private ExecutorService executorService;


    private static Map<Integer, Writer> clientMap;
    private ServerSocket server;
    // 初始化
    public Server() {
        clientMap = new HashMap<Integer, Writer>();
        executorService = Executors.newFixedThreadPool(10);
    }
    // 用户连入
    // synchronized  防止map导致的线程不安全
    public synchronized void addClient(Socket socket) throws IOException {
        int port = socket.getPort();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        clientMap.put(port,writer);
        System.out.println("客户端：" + port + "已接入连接" );
    }
    // 用户退出
    public synchronized void removeClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            clientMap.get(port).close();
            System.out.println("客户端：" + socket.getRemoteSocketAddress() + "退出");
        }
    }
    // 准备退出
    public boolean readyQuit(String msg) {
        return Objects.equals(msg,QUIT);
    }
    public synchronized void close(ServerSocket server) {
        try {
            if (server != null) {
                server.close();
                System.out.println("服务器关闭");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start() {
        try {
            server = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务器");
            while (true) {
                Socket client = server.accept();
                // ChatHandler 负责对应的客户端并转发
                executorService.execute(new ChatHandler(client,this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(server);
        }
    }
    public synchronized void forwardMsg(Socket socket,String msg) throws IOException {
        for (int port : clientMap.keySet()) {
            if (port != socket.getPort()) {
                Writer writer = clientMap.get(port);
                writer.write(msg);
                writer.flush();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
