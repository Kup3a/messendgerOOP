package comands;

import session.MessagesStorage;
import session.Session;

/**
 * Created by user on 20.10.2015.
 */
public class FindCommand implements Command {
    @Override
    public String execute(Session session, String[] args) {
        if (session.getSessionUser() == null) {
            return "you are not authorized";
        } else if (args.length == 2) {
            //MessagesStorage messagesStorage = new MessageFileStorage();
            MessagesStorage messagesStorage = session.getMessagesStorage();
            String[] allMessages = messagesStorage.getAllMessages();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < allMessages.length; i++) {
                if (allMessages[i].contains(args[1])) {
                    stringBuilder.append(allMessages[i] + "\n");
                }
            }
            return stringBuilder.toString();
        } else {
            return "This command require 1 argument";
        }
    }
}
