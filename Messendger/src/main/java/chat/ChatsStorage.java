package chat;

import session.Session;
import session.User;

import java.util.ArrayList;

/**
 * Created by user on 24.11.2015.
 */
public interface ChatsStorage {
    void storeChat(String name);

    String addUser(Chat chat, User user);

    void deleteUser(Chat chat, User user);

    //можно найти по имени, т.к. это поле в бд уникально
    Chat getChat(String name);

    ArrayList<Chat> getChatList(Session session);

    boolean isChatExist(String name);
}
