package comands;

import chat.Chat;
import chat.ChatsStorage;
import session.Session;

/**
 * Created by user on 24.11.2015.
 */
public class ChatCreateCommand implements Command {
    //ChatsStorage storage = new DBChatStorage(); //!!!вместо такого подхода теперь храним DBChatStorage в session

    @Override
    public String execute(Session session, String[] args) {

        if (session.getSessionUser() == null) {
            return "You are not authorizated";
        } else if (args.length == 2 && !session.getChatsStorage().isChatExist(args[1])) {
            //тут создаём чат с именем args[1]
            ChatsStorage storage = session.getChatsStorage();
            storage.storeChat(args[1]);
            Chat chat = storage.getChat(args[1]);
            chat.getUsers().add(session.getSessionUser());
            session.setCurrentChat(chat);
            return "Now you are in " + session.getCurrentChat().getChatName() + " chat.";
        } else if (args.length == 2) {
            return "such chat already exist";
        } else {
            return "Wrong arguments for \\create_chat command.";
        }
    }
}
