package bio.easy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Package:bio.easy
 * Description:
 * 简易的服务器
 * @author:鲍嘉鑫
 */
public class Server {
    public static final String Quit = "quit";
    public static final int DEFAULT_PORT = 8888;
    public static void main(String[] args) {
        ServerSocket server = null;
        try{
            server = new ServerSocket(8888);
            System.out.println("服务器启动,监听端口：" + DEFAULT_PORT);
            while (true) {
                Socket client = server.accept();
                System.out.println("服务器收到来自【" + client.getRemoteSocketAddress() + "】的连接");
                // 接收 client的输入
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                // 写入到client中
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                String msg = null;
                while ((msg = bufferedReader.readLine()) != null) {
                    System.out.println("客户端发送消息：" + msg);
                    bufferedWriter.write("服务器：收到消息\n");
                    bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
                System.out.println("服务器关闭");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
