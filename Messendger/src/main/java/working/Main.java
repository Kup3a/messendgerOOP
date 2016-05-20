package working;

/**
 * Created by user on 20.10.2015.
 */

public class Main {

    private static final String EXIT = "q|exit";

    public static void main(String[] args) throws Exception {

        //here client-server approach
       /* Runnable server = new working.ChatServer();
        Thread t = new Thread(server);
        t.start();*/

        // Старый старт io подхода: сначала серв, потом клиентов
//        ChatServer server = new ChatServer();
//        server.startServer();
//        System.out.println("main is over");
//
//
        ChatClient client = new ChatClient();
        client.connectToServer();

        // Старт через интерфейс CommonServer
        // nio-сервер
//        CommonServer server = new Server();
//        server.startServer();
//        server.stopServer();
        // io-сервер
//        server = new ChatServer();
//        server.startServer();
//        server.stopServer();
    }

}
