import authorization.AuthorizationService;
import authorization.DBUserStore;
import authorization.UserStore;
import chat.DBChatStorage;
import comands.ChatCreateCommand;
import comands.Command;
import comands.HistoryCommand;
import comands.LoginCommand;
import org.junit.Before;
import org.junit.Test;
import session.Session;

import static org.junit.Assert.assertEquals;

/**
 * Created by user on 15.12.2015.
 */
public class ChatCreateCommandTest {
    UserStore userStore;
    AuthorizationService authorizationService;
    Command createCommand;
    Session session;

    @Before
    public void init() {
        userStore = new DBUserStore();
        authorizationService = new AuthorizationService(userStore);
        createCommand = new ChatCreateCommand();
        session = new Session();
        session.setChatsStorage(new DBChatStorage());
        // сначала надо залогинить пользователя
        String[] logTokens = {"\\login", "test", "test"};
        Command login = new LoginCommand(authorizationService);
        login.execute(session, logTokens);
    }

    @org.junit.Ignore
    @Test
    public void createValid(){
        String[] createTokens = {"\\chat_create", "superNew"};
        String result = createCommand.execute(session, createTokens);
        assertEquals(result, "Now you are in superNew chat.");
    }

    @Test
    public void getHistory(){
        String[] hisTokens = {"\\history"};
        Command historyCom = new HistoryCommand();
        String result = historyCom.execute(session, hisTokens);
        assertEquals(result, "tester\n");
    }
}
