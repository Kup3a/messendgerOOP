package comands;

import session.MessagesStorage;
import session.Session;

/**
 * Created by user on 20.10.2015.
 */
public class HistoryCommand implements Command {
    @Override
    public String execute(Session session, String[] args) {

        StringBuilder stringBuilder = new StringBuilder();
        //MessagesStorage messagesStorage = new MessageFileStorage();
        MessagesStorage messagesStorage = session.getMessagesStorage();
        if (session.getSessionUser() == null) {
            return "You are not authorized";
        } else if (args.length == 1) {
            String[] s = messagesStorage.getAllMessages();
            for (int i = 0; i < s.length; i++) {
                stringBuilder.append(s[i] + "\n");
            }
            return stringBuilder.toString();
        } else if (args.length == 2) {
            Integer N = Integer.valueOf(args[1]);
            String[] s = messagesStorage.getNMessages(N);
            for (int i = 0; i < s.length; i++) {
                stringBuilder.append(s[i] + "\n");
            }
            return stringBuilder.toString();
        } else {
            return "Wrong arguments for \\history command.";
        }
    }
}
