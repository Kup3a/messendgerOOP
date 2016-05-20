package comands;

import chat.Chat;
import chat.ChatsStorage;
import session.Session;

/**
 * Created by user on 24.11.2015.
 */
public class ConnectToChat implements Command {
    //ChatsStorage storage = new DBChatStorage(); //!!!вместо такого подхода теперь храним DBChatStorage в session

    @Override
    public String execute(Session session, String[] args) {
        ChatsStorage storage = session.getChatsStorage();
        if (session.getSessionUser() == null) {
            return "You are not authorizated";
        } else if (args.length == 2 && storage.getChat(args[1]) != null) {
            //тут запихиваем клиета в чат с именем args[1]
            Chat chat = storage.getChat(args[1]);
            chat.getUsers().add(session.getSessionUser());
            session.setCurrentChat(chat);
            storage.addUser(chat, session.getSessionUser());
            return "Now you are in " + session.getCurrentChat().getChatName() + " chat.";
        } else if (storage.getChat(args[1]) == null) {
            return "Where is no such chat";
        } else {
            return "Wrong arguments for \\create_chat command.";
        }

    }
}
