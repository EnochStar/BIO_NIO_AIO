package nio.chatRoom;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Package:nio.chatRoom
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class UserInputHandler implements Runnable{
    private Client client;

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
