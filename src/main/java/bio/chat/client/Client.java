package bio.chat.client;

import bio.chat.server.ChatHandler;

import java.io.*;
import java.net.Socket;

/**
 * Package:bio.chat.client
 * Description:
 * 用于接收服务器的广播内容
 * @author:鲍嘉鑫
 */
public class Client {
    private Socket client;
    private BufferedReader reader;
    public boolean readyQuit(String msg) {
        return msg.equals("quit");
    }

    public void start() {
        try {
            client = new Socket("localhost",8888);
            new Thread(new UserInputHandler(this)).start();
            System.out.println("客户端：" + client.getRemoteSocketAddress() + "连入服务器");
            // 接收广播内容
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String msg = null;
            while ((msg = reader.readLine()) != null) {
                System.out.println("客户端收到其他客户端的消息：" + msg);
                if (readyQuit(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String msg) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            writer.write(msg + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
