package authorization;

import chat.ChatsStorage;
import session.MessageDBStorage;
import session.Session;
import session.User;
import tools.HashClass;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by user on 20.10.2015.
 */
public class AuthorizationService {

    private UserStore userStore;

    public AuthorizationService(UserStore userStore) {
        this.userStore = userStore;
    }

    public String login(String login, String password, Session session) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (userStore.isUserExist(login)) {
            // checkingUser - ������������, �������� ������� �� ��������� �� ������
            User checkingUser = userStore.getUser(login);
            String checkingSalt = checkingUser.getSalt();
            if (checkingUser.getHashedPassword().equals(HashClass.createHash(password.toCharArray(), HashClass.fromHex(checkingSalt)))) {
                session.setSessionUser(checkingUser);
                // ��������� ������� - ��� ���������� ��������� � �����
                //session.setMessagesStorage(new MessageFileStorage(checkingUser.getLogin()));
                //DBChatStorage storage = new DBChatStorage(); //!!!������ ������ ������� ������ ������ DBChatStorage � session
                ChatsStorage storage = session.getChatsStorage();
                session.setCurrentChat(storage.getChat("myChat"));

                // ��������� ������� - ��� ���������� ��������� � ����
                session.setMessagesStorage(new MessageDBStorage(1, checkingUser.getId()));
                return "you have authorized";
            } else {
                return "fail authorization";
            }
        } else {
            return "There is no such login.";
        }
//            1. Ask for name
//            2. Ask for password
//            3. Ask UserStore for user:  userStore.getUser(name, pass)
    }

    public String createUser(String login, String password) throws InvalidKeySpecException, NoSuchAlgorithmException, FileNotFoundException {
        if (userStore.isUserExist(login)) {
            return login + " login is already used, try another please.";
        }
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        userStore.addUser(user);
        return "You have successfully registred.";
        // 1. Ask for name
        // 2. Ask for pass
        // 3. Add user to UserStore: userStore.addUser(user)
    }

}

