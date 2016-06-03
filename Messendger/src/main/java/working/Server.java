package working;

import authorization.AuthorizationService;
import authorization.DBUserStore;
import authorization.UserStore;
import chat.ChatsStorage;
import chat.DBChatStorage;
import comands.*;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import session.Message;
import session.Session;
import session.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 */
public class Server implements Runnable, CommonServer {

    public static final int PORT = 19000;

    private Selector selector;
    // Это буфер, в который мы будем постоянно считывать сообщения разных клиентов
    private ByteBuffer readBuffer = ByteBuffer.allocate(512); // буфер, с которым будем работать

    private ExecutorService service = Executors.newFixedThreadPool(5);

    ServerSocketChannel serverSocketChannel;

    private static final Logger log = Logger.getLogger(Server.class);

    /////////////////////////////

    private void isAcceptableProccessing(SelectionKey key) throws IOException {
        System.out.println("new acceptable");
        // Создаем канал для клиента и регистрируем его в селекторе
        // Тут прводим к типу ServerSocketChannel, потому что внутри этого if'а может оказаться лишь тот
        // ключ, который Acceptable, а таковым можеть быть только serverSocketChannel
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        socketChannel.configureBlocking(false);
        Session session = new Session();
        session.setSocketChannel(socketChannel);
        session.setChatsStorage(chatsStorage);
        session.setSessionTocken(connectionAmount);
        Message m = new Message();
        m.setBody(String.valueOf(connectionAmount));
        m.setConnectionId(connectionAmount);
        m.setTime(Calendar.getInstance().getTime());
        socketChannel.write(ByteBuffer.wrap(mapper.writeValueAsBytes(m)));
        key.attach(session);
        channelSession.put(socketChannel, session);
        sessionList.put(connectionAmount, session);
        log.info("new acceptable with token = " + connectionAmount);
        connectionAmount++;
        System.out.println("accepted");
        // Для нас интересно событие, когда клиент будет писать в канал
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void isReadableProcessingSmall(Message message, Session session, SocketChannel socketChannel) {
        String mesBody = handler.handle(message.getBody(), session);
        log.info("new message = '" + mesBody + "' from clientId = " + message.getConnectionId());
        message.setBody(mesBody);
        message.setTime(Calendar.getInstance().getTime());
        try {
            byte[] answer = mapper.writeValueAsBytes(message);
            if (session.getSessionUser() != null) {
                // пользователи того чата, в котором находится текущий юзер
                ArrayList<User> usersOfClientChat = session.getCurrentChat().getUsers();
                // проходим по всем текущим сессиям, откуда берем текущих юзеров
                for (Map.Entry<Integer, Session> entry : sessionList.entrySet()) {
                    User userInSessionList = entry.getValue().getSessionUser();
                    if (usersOfClientChat.contains(userInSessionList) && !userInSessionList.equals(session.getSessionUser())) {
                        LinkedList<ByteBuffer> answers = answersByChannel.get(entry.getValue().getSocketChannel());
                        if (answers != null) {
                            answers.add(ByteBuffer.wrap(answer));
                        } else {
                            LinkedList<ByteBuffer> list = new LinkedList<>();
                            list.add(ByteBuffer.wrap(answer));
                            answersByChannel.put(entry.getValue().getSocketChannel(), list);
                        }
                        entry.getValue().getSocketChannel().keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    }
                }
            }
            LinkedList<ByteBuffer> answers = answersByChannel.get(socketChannel);
            if (answers != null) {
                answers.add(ByteBuffer.wrap(answer));
            } else {
                LinkedList<ByteBuffer> list = new LinkedList<>();
                list.add(ByteBuffer.wrap(answer));
                answersByChannel.put(socketChannel, list);
            }
            socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
            selector.wakeup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void isWritableProccessing(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            ByteBuffer b;
            while (!answersByChannel.get(socketChannel).isEmpty()) {
                b = answersByChannel.get(socketChannel).pollFirst();
                socketChannel.write(b);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Map<SocketChannel, LinkedList<ByteBuffer>> answersByChannel = new ConcurrentHashMap<>();

    private int connectionAmount = 1;
    private final String EXIT = "q|exit";
    private Map<String, Command> commands;
    private UserStore userStore;
    private AuthorizationService authService;
    private InputHandler handler;
    private Map<Integer, Session> sessionList = new HashMap<>();
    private Map<SocketChannel, Session> channelSession = new HashMap<>();
    private ChatsStorage chatsStorage;
    ObjectMapper mapper = new ObjectMapper();
    /////////////////////////////


    public Server() throws Exception {
        selector = Selector.open();

        // Это серверный сокет
        serverSocketChannel = ServerSocketChannel.open();

        // Привязали его к порту
        serverSocketChannel.socket().bind(new InetSocketAddress(PORT));

        // Должен быть неблокирующий для работы через selector
        serverSocketChannel.configureBlocking(false);

        // Нас интересует событие коннекта клиента (как и для Socket - ACCEPT)
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        /////////////////////////
        commands = new HashMap<>();
        userStore = new DBUserStore();
        authService = new AuthorizationService(userStore);
        chatsStorage = new DBChatStorage();

        Command loginCommand = new LoginCommand(authService);
        Command helpCommand = new HelpCommand(commands);
        Command historyCommand = new HistoryCommand();
        Command findCommand = new FindCommand();
        Command nickCommand = new NickCommand();
        Command chatCreateCommand = new ChatCreateCommand();
        Command chatConnectCommand = new ConnectToChat();

        commands.put("\\login", loginCommand);
        commands.put("\\help", helpCommand);
        commands.put("\\history", historyCommand);
        commands.put("\\find", findCommand);
        commands.put("\\nick", nickCommand);
        commands.put("\\create_chat", chatCreateCommand);
        commands.put("\\connect_chat", chatConnectCommand);

        handler = new InputHandler(commands);
        /////////////////////////
    }

    @Override
    public void run() {
        System.out.println("server is started");

        while (selector.isOpen()) {
            try {
                // Блокируемся до получения евента на зарегистрированных каналах
                int num = selector.select();

                // Смторим, кто сгенерил евенты
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();

                // Проходим по всем источникам
                while (it.hasNext()) {
                    SelectionKey key = it.next();

                    // Если кто-то готов присоединиться
                    if (key.isAcceptable()) {

                        isAcceptableProccessing(key);
                    } else if (key.isReadable()) {
                        // А внутри этого if'а работаем с каналами SocketChannel подключившихся клиентов
                        // По ключу получаем соответствующий канал
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        readBuffer.clear(); // чистим перед использование, т.к сейчас будем в этот буфер читать данные с канала
                        int numRead;
                        try {
                            // читаем данные в буфер
                            numRead = socketChannel.read(readBuffer);
                            String data = new String(readBuffer.array(), 0, numRead);
                            System.out.println(data + " " + numRead);
                            Message m = mapper.readValue(data, Message.class);
                            if (m.getBody() != null && m.getBody().equals(EXIT)) {
                                log.info("client disconnected");
                                break;
                            }

                            System.out.println("Handling message from clientId = " + m.getConnectionId());

                            service.submit(() -> {
                                isReadableProcessingSmall(m, channelSession.get(socketChannel), socketChannel);
                            });

//                            key.interestOps(SelectionKey.OP_WRITE);
//                            selector.wakeup();

                        } catch (IOException e) {
                            // Ошибка чтения - закроем это соединений и отменим ключ в селекторе
                            log.info("Failed to read data from channel", e);
                            key.cancel();
                            socketChannel.close();
                            break;
                        }

                        if (numRead == -1) {
                            // С нами оборвали соединение со стороны клиента
                            log.error("Failed to read data from channel (-1)");
                            key.channel().close();
                            key.cancel();
                            break;
                        }

                    } else if (key.isWritable()) {
                        log.info("[writable]");
                        // В этот if попадают те каналы, которые мы обработали в if (key.isReadable())
                        System.out.println("new writable");

                        service.submit(() -> {
                            isWritableProccessing(key);
                        });

                        // Меняем состояние канала - теперь он готов для чтения и в следующий select() он будет isReadable();
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }

                // Нужно почитстить обработанные евенты
                keys.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//    public static void main(String[] args) throws Exception {
//        Thread t = new Thread(new Server());
//        t.start();
//    }

    @Override
    public void startServer() {
        Thread t = null;
        try {
            t = new Thread(this);
            t.start();
            stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopServer() {
        Thread t = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            while (command != null) {
                if (command.equals("stop")) {
                    log.info("stop server");
                    //service.shutdown();
                    try {
                        serverSocketChannel.close();
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> it = keys.iterator();
                        while (it.hasNext()) {
                            SelectionKey key = it.next();
                            SelectableChannel channel = key.channel();
                            if (channel instanceof SocketChannel) {
                                SocketChannel socketChannel = (SocketChannel) channel;

                                socketChannel.close();

                                key.cancel();
                            }
                        }
                        selector.wakeup();
                        selector.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
        t.start();
    }
}