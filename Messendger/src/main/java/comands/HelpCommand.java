package comands;

import session.Session;

import java.util.Map;

/**
 * Created by user on 20.10.2015.
 */
public class HelpCommand implements Command {

    private Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public String execute(Session session, String[] args) {
        return "Available list of commands:\n" +
                "\\login <user_login> <password> - for authorization\n" +
                "\\login new <user_login> <password> - for register new user\n" +
                "\\history - for getting history of messages\n" +
                "\\find <key_word> - for searching through the history\n" +
                "\\create_chat <chat_name> - create new chat with chat_name name\n" +
                "\\connect_chat <chat_name> - connect you to chat with chat_name name\n" +
                "q|exit - for leaving chat";
    }
}
