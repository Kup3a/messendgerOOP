package session;

import chat.Chat;
import chat.ChatsStorage;

import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * Created by user on 20.10.2015.
 */
public class Session {

    private User sessionUser;
    //������������� � �������� ������ ����� ������� ��� ��� � ������ MessageStorage ��������������� ������������
    private MessagesStorage messagesStorage;
    private Chat currentChat;
    private Socket clientSocket;
    private ChatsStorage chatsStorage;
    private SocketChannel socketChannel;
    private int sessionTocken;

    public MessagesStorage getMessagesStorage() {
        return messagesStorage;
    }

    public void setMessagesStorage(MessagesStorage messagesStorage) {
        this.messagesStorage = messagesStorage;
    }


    public Session() {    }

    public User getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(User sessionUser) {
        this.sessionUser = sessionUser;
    }

    public Chat getCurrentChat() {
        return currentChat;
    }

    public void setCurrentChat(Chat currentChat) {
        this.currentChat = currentChat;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public ChatsStorage getChatsStorage() {
        return chatsStorage;
    }

    public void setChatsStorage(ChatsStorage chatsStorage) {
        this.chatsStorage = chatsStorage;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public int getSessionTocken() {
        return sessionTocken;
    }

    public void setSessionTocken(int sessionTocken) {
        this.sessionTocken = sessionTocken;
    }
}
