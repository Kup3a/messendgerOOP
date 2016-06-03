package working;

import authorization.AuthorizationService;
import authorization.DBUserStore;
import authorization.UserStore;
import chat.ChatsStorage;
import chat.DBChatStorage;
import comands.*;
import org.codehaus.jackson.map.ObjectMapper;
import session.Message;
import session.Session;
import session.User;
import tools.HashClass;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by user on 27.10.2015.
 */
public class ChatServer implements Runnable,CommonServer {

    private int connectionAmount = 1;
    //������ ������� � ������ initServer
    ExecutorService executors = Executors.newFixedThreadPool(5);

    private final String EXIT = "q|exit";
    private Map<String, Command> commands;
    private UserStore userStore;
    private AuthorizationService authService;
    private InputHandler handler;
    private ServerSocket serverSocket;
    private Map<Integer, Session> sessionList = new HashMap<>();
    private ChatsStorage chatsStorage;

//    ExecutorService executorService = new Executors.newFixedThreadPool();


    private void sendToChat(String mes, Session session) {
        ArrayList<User> usersOfClientChat = session.getCurrentChat().getUsers();
        for (Map.Entry<Integer, Session> entry : sessionList.entrySet()) {
            User userInSessionList = entry.getValue().getSessionUser();
            if (usersOfClientChat.contains(userInSessionList) && ! userInSessionList.equals(session.getSessionUser())) {
                try {
                    entry.getValue().getClientSocket().getOutputStream().write(mes.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void startServer() {
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

        //���������� ��������� � ��� ���� �� ��� ����������, � ��� ���������
        //�������� ����������� ��������� � ���� ������� Session
        handler = new InputHandler(commands);

        try {
            //���� ������-����� �� ��� ����, ������ �� ������� �������� �� ����� ��������
            serverSocket = new ServerSocket(3129, 0, InetAddress.getByName("localhost"));
            //������ ����� ������ ����, ������� ����� ����� ������ ������� (� ��� ��������� ����� connectionId = 1
            Thread connection = new Thread(this);
            connection.start();

//            executorService = new Executors.newFixedThreadPool();
//            executorService.execute(new Thread(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            //����������� ��������, ������� ��� ���������� �����������
            Socket socket = serverSocket.accept();
            System.out.println("\nNew server thread for client " + connectionAmount + " inited successfully.");
            int threadId = connectionAmount;
            Session session = new Session();
            //��������: ���� ������� ��� �� �������� ����� ���� - �����
            session.setClientSocket(socket);
            session.setChatsStorage(chatsStorage);
            sessionList.put(threadId, session);
            //������� ����������� �������������
            connectionAmount++;
            //� ������ ����� ����� �������� ����� ���� ��� ���������� ������� => �������������� ������� � connectionAmount ���
            Thread connection = new Thread(this);
            connection.start();

//            executorService.execute(new Thread(this));

            //��� ����� ���� �������� ���� ������� �����, json-���������� � ������ (����)
            InputStream is = socket.getInputStream();
            ObjectMapper mapper = new ObjectMapper();


            while (true) {
                // ������ ������ � 64 ���������
                byte buf[] = new byte[64*1024];
                // ������ 64�� �� �������, ��������� - ���-�� ������� �������� ������
                int r = is.read(buf);
                // ������ ������, ���������� ���������� �� ������� ����������
                String data = new String(buf, 0, r);
                // ������ - ��� json, ������� ���� ������������� � ������ � Message
                Message m = mapper.readValue(data, Message.class);
                // ������ �� ��������� ����� ���� ���������-�������
                if (m.getBody() != null && m.getBody().equals(EXIT)) {
                    break;
                }
                //�� ��������� �� ���������� ��������� ������ ����� ����, ��� ������� ��������� connectionId
                //if-���� ������������ ��� ��� �������� connectionId ������� ��������
                if (m.getConnectionId() != 0) {
                    System.out.println("\nHandling message from clientId = " + m.getConnectionId());
                    //��� ������� ��������� �� ��������� ����� ������������ � �������� � ���� ������� Session
                    String answer;
                    if (! session.token.equals(m.token) ) {
                        answer = "Try to reconnect please";
                    } else {
                        answer = handler.handle(m.getBody(), session);
                    }
                    socket.getOutputStream().write(answer.getBytes());
                    if (session.getSessionUser() != null) {
                        sendToChat(answer, session);
                    }
                //else-���� ����������� ��� ������ ��������� �� �������
                } else {
                    System.out.println("\nSending connectionId = " + threadId + " to the new client from server.");
                    session.token = HashClass.getToken();
                    System.out.println("token " + session.token);
                    String ans = "connectionAmount " + String.valueOf(threadId) + " " + session.token;
                    socket.getOutputStream().write(ans.getBytes());
                    //initNewConnection();
                }
            }
        } catch (IOException e) {
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
                    System.exit(0);
                }
            }
        });
        t.start();
    }
}
