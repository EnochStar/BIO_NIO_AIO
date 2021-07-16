package bio.chat.client;

import java.io.*;
import java.net.Socket;

/**
 * Package:bio.chat.client
 * Description:
 * 用于接收用户输入
 * @author:鲍嘉鑫
 */
public class UserInputHandler implements Runnable{
    Client client;

    public UserInputHandler(Client client) {
        this.client = client;
    }


    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
            while ((msg = in.readLine()) != null) {
                client.send(msg);
                if (msg.equals("quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
