import authorization.AuthorizationService;
import authorization.DBUserStore;
import authorization.UserStore;
import chat.DBChatStorage;
import comands.Command;
import comands.LoginCommand;
import org.junit.Before;
import org.junit.Test;
import session.Session;
import static org.junit.Assert.assertEquals;
/**
 * Created by user on 15.12.2015.
 */
public class LoginCommandTes {
    UserStore userStore;
    AuthorizationService authorizationService;
    Command loginCommand;
    Session session;

    @Before
    public void init() {
        userStore = new DBUserStore();
        authorizationService = new AuthorizationService(userStore);
        loginCommand = new LoginCommand(authorizationService);
        session = new Session();
        session.setChatsStorage(new DBChatStorage());

    }

    @Test
    public void realUser (){
        String[] tokens = {"\\login", "q", "w"};
        String result = loginCommand.execute(session, tokens);
        assertEquals(result, "you have authorized");
    }

    @Test
    public void existingUserRegistr(){
        String[] tokens = {"\\login", "new", "q", "qwerty"};
        String result = loginCommand.execute(session, tokens);
        assertEquals(result, "q login is already used, try another please.");
    }

    @Test
    public void wrongUserLogin(){
        String[] tokens = {"\\login", "w", "w"};
        String result = loginCommand.execute(session, tokens);
        assertEquals(result, "There is no such login.");
    }

    @Test
    public void wrongUserPassword(){
        String[] tokens = {"\\login", "q", "www"};
        String result = loginCommand.execute(session, tokens);
        assertEquals(result, "fail authorization");
    }
}
