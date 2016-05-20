package authorization;

import session.User;
import tools.FileWork;
import tools.HashClass;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

/**
 * Created by user on 20.10.2015.
 */
public class FileUserStore implements UserStore {

    @Override
    public boolean isUserExist(String login) {
        try {
            String storage = FileWork.readFile("D:\\Projects\\IdeaProjects\\technotrackChat\\src\\storage.txt");
            String[] s = storage.split("\\s");
            for (int i = 0; i < s.length / 4; i++) {
                if (login.equals(s[4 * i])){
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void addUser(User user) {
        System.out.println("making new user in fileuserstorage");
        try {
            FileWork.writeToFile(user.getLogin() + " " + HashClass.createHash(user.getPassword()), "D:\\Projects\\IdeaProjects\\technotrackChat\\src\\storage.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }

    @Override
    public User getUser(String name) {
        try {
            String storage = FileWork.readFile("D:\\Projects\\IdeaProjects\\technotrackChat\\src\\storage.txt");
            HashMap<String, User> map = new HashMap();
            String[] s = storage.split("\\s");
            for (int i = 0; i < s.length / 4; i++) {
                User t = new User(s[4 * i], s[4 * i + 1], s[4 * i + 2], s[4 * i + 3]);
                map.put(t.getLogin(), t);
            }
            if (map.get(name) == null) {
                return null;
            } else {
                return map.get(name);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
