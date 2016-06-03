package working;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import session.Message;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 *
 */
public class Client {
    // А это сообщение никогда не удаляется. У него только меняется текст и время.
    private Message m = new Message();

    public static final int PORT = 19000;

    private Selector selector;
    private SocketChannel channel;
    private ByteBuffer buffer = ByteBuffer.allocate(512);

    private static final Logger log = Logger.getLogger(Client.class);

    BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);


    public void init() throws Exception {

        m.setConnectionId(0);

        // Слушаем ввод данных с консоли
        Thread t = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String line;
            SelectionKey key = channel.keyFor(selector);
            while (true) {
                // эта отдельная нить будет блокироваться на следующей строке
                line = scanner.nextLine();
                if ("q".equals(line)) {
                    log.info("client " + m.getConnectionId() + " exit");
                    System.exit(0);
                }
                try {
                    queue.put(line);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }

                // Будим селектор
// ???          интересно, почему каждый раз нужно обновлять interestOps? он как-то сбрасывается после select() ?
                key.interestOps(SelectionKey.OP_WRITE);
                selector.wakeup();
            }
        });
        //t.start();


        selector = Selector.open();
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_CONNECT);

        channel.connect(new InetSocketAddress("localhost", PORT));
        System.out.println("connected to server");
        while (true) {
            int num = selector.select();
            ObjectMapper mapper = new ObjectMapper();


            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey sKey = keyIterator.next();

                if (sKey.isConnectable()) {
                    log.info("connection is finished");
                    System.out.println("connection is finished");
                    channel.finishConnect();
                    t.start();
                    // теперь в канал можно писать
                    sKey.interestOps(SelectionKey.OP_WRITE);
                } else if (sKey.isReadable()) {
                    log.info("[readable]");
                    buffer.clear();
                    int numRead = channel.read(buffer);
                    if (numRead < 0) {
                        break;
                    }
                    buffer.flip();
                    String s = new String(buffer.array(), 0, numRead);
                    // этот объект служит только для распечатки сообщения в консоль или (если мы только что подключились)
                    // для задания ConnectionId
                    Message message = mapper.readValue(s, Message.class);
                    s = message.getBody();
                    if (m.getConnectionId() != 0) {
                        System.out.println("from server: " + s);
                        log.info("From server: " + s);
                    } else {
                        m.setConnectionId(Integer.parseInt(s));
                    }
                    buffer.clear();
                    sKey.interestOps(SelectionKey.OP_WRITE);
                } else if (sKey.isWritable()) {
                    log.info("[writable]");
                    String line = queue.poll();
                    if (line != null) {
                        m.setBody(line);
                        m.setTime(Calendar.getInstance().getTime());
                        line = mapper.writeValueAsString(m);
                        System.out.println(line);
                        channel.write(ByteBuffer.wrap(line.getBytes()));
                    }
                    // Ждем записи в канал
                    sKey.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Проблемы на стороне сервера. Попробуйте подключиться позднее.");
            System.exit(0);
        }
    }
}