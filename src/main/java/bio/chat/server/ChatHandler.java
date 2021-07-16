package bio.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Package:bio.chat.server
 * Description:
 * 用于广播
 * @author:鲍嘉鑫
 */
public class ChatHandler implements Runnable {
    private Socket socket;
    private Server server;

    public ChatHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // 添加新用户
            server.addClient(socket);
            String msg = null;
            // 读取用户输入
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((msg = reader.readLine()) != null) {
                String fMsg = "客户端：" + socket.getPort() + "输出内容为：" + msg +"\n";
                if (server.readyQuit(msg)) {
                    break;
                }
                server.forwardMsg(socket,fMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
