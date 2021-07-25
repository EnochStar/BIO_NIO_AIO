package aio.chat;

import org.omg.PortableServer.POA;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Package:aio.chat
 * Description:
 *
 * @author:鲍嘉鑫
 */
public class Client {
    private static final String DEFAULT_LOCAL_HOST = "localhost";
    private static final int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFER = 1024;
    private static final int THREADPOOL_SIZE = 8;
    private Charset charSet = Charset.forName("UTF-8");


    private String localhost;
    private int port;
    private AsynchronousSocketChannel clientChannel;

    public Client() {
        this(DEFAULT_LOCAL_HOST, DEFAULT_PORT);
    }

    public Client(String localhost, int port) {
        this.localhost = localhost;
        this.port = port;
    }

    public void start() {
        try {
            clientChannel = AsynchronousSocketChannel.open();
            Future<Void> client = clientChannel.connect(new InetSocketAddress(localhost, port));
            client.get();

            new Thread(new AioUserInputHandler(this)).start();


            ByteBuffer buffer = ByteBuffer.allocate(BUFFER);
            // 读取传来的值
            while (true) {
                Future<Integer> read = clientChannel.read(buffer);
                int result = read.get();
                if (result <= 0) {
                    System.out.println("服务器断开");
                    close(clientChannel);
                    System.exit(1);
                }else{
                    buffer.flip();
                    String msg = String.valueOf(charSet.decode(buffer));
                    buffer.clear();
                    System.out.println(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String msg) {
        if (msg.isEmpty()) {
            return;
        }
        ByteBuffer buffer = charSet.encode(msg);
        Future<Integer> write = clientChannel.write(buffer);
        try{
            write.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class AioUserInputHandler implements Runnable{

        private Client client;

        public AioUserInputHandler(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String s = reader.readLine();
                    client.send(s);
                    if (readyToQuit(s)) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private boolean readyToQuit(String msg) {
            return QUIT.equals(msg);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
