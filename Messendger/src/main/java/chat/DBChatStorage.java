package chat;

import session.Session;
import session.User;
import tools.JDBCWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 24.11.2015.
 */
public class DBChatStorage implements ChatsStorage {
    /**
     * Чтобы не подтягивать чаты каждый раз из базы, будем хранить на сервере пул чатов.
     * Сюда чат может попасть только в том случае, если он когда-либо запрашивался из базы и был там найден.
     */
    private Map<String, Chat> poolOfChats = new HashMap<>();

    private JDBCWorker jdbcWorker = new JDBCWorker();

    @Override
    public void storeChat(String name) {
        jdbcWorker.storeChat(name);
    }

    @Override
    public String addUser(Chat chat, User user) {
        return jdbcWorker.addUserToChat(chat, user);
    }

    @Override
    public void deleteUser(Chat chat, User user) {

    }

    @Override
    public Chat getChat(String name) {
        if (poolOfChats.containsKey(name)) {
            return poolOfChats.get(name);
        } else {
            Chat chat = jdbcWorker.getChat(name);
            if (chat != null) {
                poolOfChats.put(chat.getChatName(), chat);
                return chat;
            }
        }
        return null;
    }

    /**
     * Чтобы получить список всех существующих в БД чатов, недостаточно отдать poolOfChats, т.к. туда чаты попадают
     * только тогда, когда их запросили методом getChatю.
     * Поэтому делаем новый метод.
     * @param session
     * @return
     */
    @Override
     public ArrayList<Chat> getChatList(Session session) {
        return jdbcWorker.getChatList(session);
    }

    @Override
    public boolean isChatExist(String name) {
        if (getChat(name) == null) {
            return false;
        }
        return true;
    }
}
