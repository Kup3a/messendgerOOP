package authorization;

import session.User;
import tools.JDBCWorker;

import java.sql.SQLException;

/**
 * Created by user on 15.11.2015.
 */
public class DBUserStore implements UserStore {
    private JDBCWorker toDB = new JDBCWorker();

    @Override
    public boolean isUserExist(String name) {
        boolean ans = false;
        try {
            if (toDB.getUserFromDB(name) != null) {
                ans = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ans;
    }

    @Override
    public void addUser(User user) {
        try {
            toDB.addUserToDB(user);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUser(String name) {
        try {
            return toDB.getUserFromDB(name);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
