package bio.easy;

import java.io.*;
import java.net.Socket;

/**
 * Package:bio.easy
 * Description:
 * 只支持一对一
 * @author:鲍嘉鑫
 */
public class Client {
    public static void main(String[] args) {
        Socket client = null;
        try {
            client = new Socket("127.0.0.1",8888);
            // 输出到服务器端
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            // 输入流
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String msg = in.readLine();
                // 消息发给服务器
                bufferedWriter.write(msg + "\n");
                bufferedWriter.flush();
                String res = bufferedReader.readLine();
                System.out.println("服务器：" + res);
                if (bufferedWriter.equals("QUIT"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
